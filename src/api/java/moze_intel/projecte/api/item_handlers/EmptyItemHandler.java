package moze_intel.projecte.api.item_handlers;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An always empty item handler.
 */
public class EmptyItemHandler implements IItemHandlerModifiable {

	public static final EmptyItemHandler INSTANCE = new EmptyItemHandler();

	protected EmptyItemHandler() {
	}

	@Override
	public int getSlots() {
		return 0;
	}

	@NotNull
	@Override
	public ItemStack getStackInSlot(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
	}

	@NotNull
	@Override
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		return stack;
	}

	@NotNull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 0;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return false;
	}
}
