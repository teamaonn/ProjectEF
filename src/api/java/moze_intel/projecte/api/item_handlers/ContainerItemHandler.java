package moze_intel.projecte.api.item_handlers;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Adapts a vanilla {@link Container} (for example the player inventory) to the {@link IItemHandlerModifiable} interface.
 */
public class ContainerItemHandler implements IItemHandlerModifiable {

	protected final Container container;

	public ContainerItemHandler(Container container) {
		this.container = container;
	}

	public Container getContainer() {
		return container;
	}

	@Override
	public int getSlots() {
		return container.getContainerSize();
	}

	@NotNull
	@Override
	public ItemStack getStackInSlot(int slot) {
		return container.getItem(slot);
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		container.setItem(slot, stack);
	}

	@NotNull
	@Override
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (stack.isEmpty() || !isItemValid(slot, stack)) {
			return stack;
		}
		ItemStack existing = container.getItem(slot);
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
				container.setItem(slot, reachedLimit ? stack.copyWithCount(limit) : stack.copy());
			} else {
				existing.grow(reachedLimit ? limit : stack.getCount());
				container.setChanged();
			}
		}
		return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@NotNull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0) {
			return ItemStack.EMPTY;
		}
		ItemStack existing = container.getItem(slot);
		if (existing.isEmpty()) {
			return ItemStack.EMPTY;
		}
		int toExtract = Math.min(amount, existing.getMaxStackSize());
		if (simulate) {
			return existing.copyWithCount(Math.min(existing.getCount(), toExtract));
		}
		ItemStack extracted = container.removeItem(slot, toExtract);
		container.setChanged();
		return extracted;
	}

	@Override
	public int getSlotLimit(int slot) {
		return container.getMaxStackSize();
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return container.canPlaceItem(slot, stack);
	}
}
