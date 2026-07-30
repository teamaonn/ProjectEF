package moze_intel.projecte.api.item_handlers;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A slot based item inventory abstraction, modeled after the item handler concept most modded inventories are built around.
 * <p>
 * Slots are referenced by index. Prefer {@link #insertItem(int, ItemStack, boolean)} / {@link #extractItem(int, int, boolean)}
 * over mutating a stack returned by {@link #getStackInSlot(int)}; callers that do mutate one in place must call
 * {@link #markSlotChanged(int)} afterwards, otherwise handlers whose backing store needs an explicit change signal will
 * never sync.
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

	/**
	 * Called by consumers that mutate a stack returned by {@link #getStackInSlot(int)} in place, rather than going
	 * through {@link #insertItem(int, ItemStack, boolean)} or {@link #extractItem(int, int, boolean)}.
	 * <p>
	 * Defaults to a no-op, which is correct for handlers whose backing store is already kept in sync by other means
	 * (a vanilla {@link net.minecraft.world.Container} broadcast, for example). Implementations backed by a store that
	 * needs an explicit change signal must override this.
	 *
	 * @param slot Slot whose stack was mutated in place.
	 *
	 * @implNote This is deliberately not named {@code onContentsChanged}: {@link ItemStackHandler} already declares a
	 * {@code protected} method by that name, so a same-signature interface method would narrow its access and fail to
	 * compile. {@link ItemStackHandler} bridges this method onto that hook instead.
	 */
	default void markSlotChanged(int slot) {
	}
}
