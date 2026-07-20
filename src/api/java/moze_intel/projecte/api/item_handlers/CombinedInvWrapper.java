package moze_intel.projecte.api.item_handlers;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Joins multiple {@link IItemHandlerModifiable}s into a single handler with a combined slot index space.
 */
public class CombinedInvWrapper implements IItemHandlerModifiable {

	protected final IItemHandlerModifiable[] itemHandler;
	protected final int[] baseIndex;
	protected final int slotCount;

	public CombinedInvWrapper(IItemHandlerModifiable... itemHandler) {
		this.itemHandler = itemHandler;
		this.baseIndex = new int[itemHandler.length];
		int index = 0;
		for (int i = 0; i < itemHandler.length; i++) {
			index += itemHandler[i].getSlots();
			baseIndex[i] = index;
		}
		this.slotCount = index;
	}

	protected int getIndexForSlot(int slot) {
		if (slot < 0) {
			return -1;
		}
		for (int i = 0; i < baseIndex.length; i++) {
			if (slot - baseIndex[i] < 0) {
				return i;
			}
		}
		return -1;
	}

	protected IItemHandlerModifiable getHandlerFromIndex(int index) {
		if (index < 0 || index >= itemHandler.length) {
			return EmptyItemHandler.INSTANCE;
		}
		return itemHandler[index];
	}

	protected int getSlotFromIndex(int slot, int index) {
		if (index <= 0 || index >= baseIndex.length) {
			return slot;
		}
		return slot - baseIndex[index - 1];
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		int index = getIndexForSlot(slot);
		IItemHandlerModifiable handler = getHandlerFromIndex(index);
		handler.setStackInSlot(getSlotFromIndex(slot, index), stack);
	}

	@Override
	public int getSlots() {
		return slotCount;
	}

	@NotNull
	@Override
	public ItemStack getStackInSlot(int slot) {
		int index = getIndexForSlot(slot);
		IItemHandlerModifiable handler = getHandlerFromIndex(index);
		return handler.getStackInSlot(getSlotFromIndex(slot, index));
	}

	@NotNull
	@Override
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		int index = getIndexForSlot(slot);
		IItemHandlerModifiable handler = getHandlerFromIndex(index);
		return handler.insertItem(getSlotFromIndex(slot, index), stack, simulate);
	}

	@NotNull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		int index = getIndexForSlot(slot);
		IItemHandlerModifiable handler = getHandlerFromIndex(index);
		return handler.extractItem(getSlotFromIndex(slot, index), amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		int index = getIndexForSlot(slot);
		IItemHandlerModifiable handler = getHandlerFromIndex(index);
		return handler.getSlotLimit(getSlotFromIndex(slot, index));
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		int index = getIndexForSlot(slot);
		IItemHandlerModifiable handler = getHandlerFromIndex(index);
		return handler.isItemValid(getSlotFromIndex(slot, index), stack);
	}
}
