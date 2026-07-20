package moze_intel.projecte.api.item_handlers;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A slot based item inventory abstraction, modeled after the item handler concept most modded inventories are built around.
 * <p>
 * Slots are referenced by index, stacks returned by this interface <strong>must not</strong> be modified by callers.
 */
public interface IItemHandler {

	/**
	 * @return The number of slots in this handler.
	 */
	int getSlots();

	/**
	 * Gets the stack in the given slot. The returned stack must be treated as immutable by the caller!
	 *
	 * @param slot Slot to query.
	 *
	 * @return The stack in the slot, {@link ItemStack#EMPTY} if empty.
	 */
	@NotNull
	ItemStack getStackInSlot(int slot);

	/**
	 * Attempts to insert the given stack into the given slot.
	 *
	 * @param slot     Slot to insert into.
	 * @param stack    Stack to insert. Not modified by this method.
	 * @param simulate If true, the insertion is only simulated and no state is modified.
	 *
	 * @return The remaining stack that was not inserted, {@link ItemStack#EMPTY} if the entire stack fit.
	 */
	@NotNull
	ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate);

	/**
	 * Attempts to extract up to the given amount from the given slot.
	 *
	 * @param slot     Slot to extract from.
	 * @param amount   Maximum amount to extract.
	 * @param simulate If true, the extraction is only simulated and no state is modified.
	 *
	 * @return The stack that was (or would be) extracted, {@link ItemStack#EMPTY} if nothing could be extracted.
	 */
	@NotNull
	ItemStack extractItem(int slot, int amount, boolean simulate);

	/**
	 * @param slot Slot to query.
	 *
	 * @return The maximum stack size the given slot can hold, regardless of what item is in it.
	 */
	int getSlotLimit(int slot);

	/**
	 * @param slot  Slot to query.
	 * @param stack Stack to test.
	 *
	 * @return Whether the given stack is generally valid for the given slot.
	 */
	boolean isItemValid(int slot, @NotNull ItemStack stack);
}
