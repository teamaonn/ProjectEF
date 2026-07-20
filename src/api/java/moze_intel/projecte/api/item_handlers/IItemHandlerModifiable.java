package moze_intel.projecte.api.item_handlers;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link IItemHandler} that additionally supports directly overwriting the contents of a slot, bypassing insertion checks.
 */
public interface IItemHandlerModifiable extends IItemHandler {

	/**
	 * Directly sets the stack in the given slot, bypassing any insertion validation. Intended for internal or trusted use such as loading data or container
	 * synchronization.
	 *
	 * @param slot  Slot to set.
	 * @param stack Stack to set the slot to. May be {@link ItemStack#EMPTY}.
	 */
	void setStackInSlot(int slot, @NotNull ItemStack stack);
}
