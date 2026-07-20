package moze_intel.projecte.api.item_handlers;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Insertion and monitoring helpers for {@link IItemHandler}s.
 */
public final class ItemHandlerHelper {

	private ItemHandlerHelper() {
	}

	/**
	 * Inserts the given stack into the handler, trying every slot in order.
	 *
	 * @return The remainder that did not fit.
	 */
	@NotNull
	public static ItemStack insertItem(@Nullable IItemHandler dest, @NotNull ItemStack stack, boolean simulate) {
		if (dest == null || stack.isEmpty()) {
			return stack;
		}
		for (int slot = 0, slots = dest.getSlots(); slot < slots; slot++) {
			stack = dest.insertItem(slot, stack, simulate);
			if (stack.isEmpty()) {
				return ItemStack.EMPTY;
			}
		}
		return stack;
	}

	/**
	 * Inserts the given stack into the handler, first topping off existing matching stacks and then filling empty slots.
	 *
	 * @return The remainder that did not fit.
	 */
	@NotNull
	public static ItemStack insertItemStacked(@Nullable IItemHandler dest, @NotNull ItemStack stack, boolean simulate) {
		if (dest == null || stack.isEmpty()) {
			return stack;
		}
		if (!stack.isStackable()) {
			return insertItem(dest, stack, simulate);
		}
		int slots = dest.getSlots();
		//First pass: top off existing stacks of the same item
		for (int slot = 0; slot < slots; slot++) {
			ItemStack existing = dest.getStackInSlot(slot);
			if (!existing.isEmpty() && ItemStack.isSameItemSameComponents(existing, stack)) {
				stack = dest.insertItem(slot, stack, simulate);
				if (stack.isEmpty()) {
					return ItemStack.EMPTY;
				}
			}
		}
		//Second pass: fill empty slots
		for (int slot = 0; slot < slots; slot++) {
			if (dest.getStackInSlot(slot).isEmpty()) {
				stack = dest.insertItem(slot, stack, simulate);
				if (stack.isEmpty()) {
					return ItemStack.EMPTY;
				}
			}
		}
		return stack;
	}

	/**
	 * Calculates the comparator strength of the given inventory, mirroring the vanilla container redstone signal calculation.
	 */
	public static int calcRedstoneFromInventory(@Nullable IItemHandler inv) {
		if (inv == null) {
			return 0;
		}
		int slots = inv.getSlots();
		if (slots == 0) {
			return 0;
		}
		float proportion = 0.0F;
		boolean empty = true;
		for (int slot = 0; slot < slots; slot++) {
			ItemStack stack = inv.getStackInSlot(slot);
			if (!stack.isEmpty()) {
				proportion += stack.getCount() / (float) Math.min(inv.getSlotLimit(slot), stack.getMaxStackSize());
				empty = false;
			}
		}
		proportion = proportion / slots;
		return Mth.floor(proportion * 14.0F) + (empty ? 0 : 1);
	}
}
