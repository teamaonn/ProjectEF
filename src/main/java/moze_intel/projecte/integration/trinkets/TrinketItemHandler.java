package moze_intel.projecte.integration.trinkets;

import dev.emi.trinkets.api.TrinketInventory;
import java.util.List;
import moze_intel.projecte.api.item_handlers.IItemHandlerModifiable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Exposes a player's Trinkets inventories as a ProjectE {@link IItemHandlerModifiable} so the loader-neutral
 * curio-polling helpers (fuel consumption, the Repair Talisman, hotbar-or-accessory ability checks) can see worn
 * accessories. Slots map one-to-one onto the underlying {@link TrinketInventory} containers, so the stacks returned by
 * {@link #getStackInSlot(int)} are the live worn stacks and may be mutated in place, matching the previous Curios behavior.
 */
public class TrinketItemHandler implements IItemHandlerModifiable {

	private final TrinketInventory[] slotInventory;
	private final int[] slotLocalIndex;

	public TrinketItemHandler(List<TrinketInventory> inventories) {
		int size = 0;
		for (TrinketInventory inventory : inventories) {
			size += inventory.getContainerSize();
		}
		this.slotInventory = new TrinketInventory[size];
		this.slotLocalIndex = new int[size];
		int slot = 0;
		for (TrinketInventory inventory : inventories) {
			for (int local = 0, count = inventory.getContainerSize(); local < count; local++) {
				slotInventory[slot] = inventory;
				slotLocalIndex[slot] = local;
				slot++;
			}
		}
	}

	@Override
	public int getSlots() {
		return slotInventory.length;
	}

	@NotNull
	@Override
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return slotInventory[slot].getItem(slotLocalIndex[slot]);
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		validateSlotIndex(slot);
		TrinketInventory inventory = slotInventory[slot];
		inventory.setItem(slotLocalIndex[slot], stack);
		inventory.markUpdate();
	}

	@NotNull
	@Override
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		} else if (!isItemValid(slot, stack)) {
			return stack;
		}
		validateSlotIndex(slot);
		TrinketInventory inventory = slotInventory[slot];
		int local = slotLocalIndex[slot];
		ItemStack existing = inventory.getItem(local);
		int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
		if (!existing.isEmpty()) {
			if (!ItemStack.isSameItemSameComponents(stack, existing)) {
				return stack;
			}
			limit -= existing.getCount();
		}
		if (limit <= 0) {
			return stack;
		}
		boolean reachedLimit = stack.getCount() > limit;
		if (!simulate) {
			if (existing.isEmpty()) {
				inventory.setItem(local, reachedLimit ? stack.copyWithCount(limit) : stack.copy());
			} else {
				existing.grow(reachedLimit ? limit : stack.getCount());
			}
			inventory.markUpdate();
		}
		return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@NotNull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0) {
			return ItemStack.EMPTY;
		}
		validateSlotIndex(slot);
		TrinketInventory inventory = slotInventory[slot];
		int local = slotLocalIndex[slot];
		ItemStack existing = inventory.getItem(local);
		if (existing.isEmpty()) {
			return ItemStack.EMPTY;
		}
		int toExtract = Math.min(amount, existing.getMaxStackSize());
		if (existing.getCount() <= toExtract) {
			if (!simulate) {
				inventory.setItem(local, ItemStack.EMPTY);
				inventory.markUpdate();
			}
			return existing.copy();
		}
		if (!simulate) {
			inventory.setItem(local, existing.copyWithCount(existing.getCount() - toExtract));
			inventory.markUpdate();
		}
		return existing.copyWithCount(toExtract);
	}

	@Override
	public int getSlotLimit(int slot) {
		return Item.ABSOLUTE_MAX_STACK_SIZE;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return true;
	}

	private void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= slotInventory.length) {
			throw new IndexOutOfBoundsException("Slot " + slot + " not in valid range - [0," + slotInventory.length + ")");
		}
	}
}
