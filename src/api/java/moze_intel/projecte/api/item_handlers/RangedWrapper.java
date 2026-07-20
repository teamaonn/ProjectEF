package moze_intel.projecte.api.item_handlers;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Exposes a contiguous slot range [minSlot, maxSlotExclusive) of another {@link IItemHandlerModifiable}.
 */
public class RangedWrapper implements IItemHandlerModifiable {

	private final IItemHandlerModifiable compose;
	private final int minSlot;
	private final int maxSlot;

	public RangedWrapper(IItemHandlerModifiable compose, int minSlot, int maxSlotExclusive) {
		if (maxSlotExclusive < minSlot) {
			throw new IllegalArgumentException("Attempted to create a ranged wrapper with an invalid range: [" + minSlot + "," + maxSlotExclusive + ")");
		}
		this.compose = compose;
		this.minSlot = minSlot;
		this.maxSlot = maxSlotExclusive;
	}

	@Override
	public int getSlots() {
		return maxSlot - minSlot;
	}

	@NotNull
	@Override
	public ItemStack getStackInSlot(int slot) {
		if (checkSlot(slot)) {
			return compose.getStackInSlot(slot + minSlot);
		}
		return ItemStack.EMPTY;
	}

	@NotNull
	@Override
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (checkSlot(slot)) {
			return compose.insertItem(slot + minSlot, stack, simulate);
		}
		return stack;
	}

	@NotNull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (checkSlot(slot)) {
			return compose.extractItem(slot + minSlot, amount, simulate);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		if (checkSlot(slot)) {
			compose.setStackInSlot(slot + minSlot, stack);
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		if (checkSlot(slot)) {
			return compose.getSlotLimit(slot + minSlot);
		}
		return 0;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		if (checkSlot(slot)) {
			return compose.isItemValid(slot + minSlot, stack);
		}
		return false;
	}

	private boolean checkSlot(int localSlot) {
		return localSlot >= 0 && localSlot + minSlot < maxSlot;
	}
}
