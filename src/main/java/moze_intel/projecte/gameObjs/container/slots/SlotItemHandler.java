package moze_intel.projecte.gameObjs.container.slots;

import moze_intel.projecte.api.item_handlers.IItemHandler;
import moze_intel.projecte.api.item_handlers.IItemHandlerModifiable;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Clean-room implementation of {@link Slot} backed by an {@link IItemHandler}.
 * <p>
 * Adapted from NeoForge's SlotItemHandler to work with ProjectEF's own IItemHandler interface.
 * All methods delegate to the underlying handler rather than a vanilla Container.
 */
public class SlotItemHandler extends Slot {

	private static final SimpleContainer EMPTY_CONTAINER = new SimpleContainer(0);
	protected final IItemHandler handler;
	protected final int index;

	public SlotItemHandler(IItemHandler handler, int index, int x, int y) {
		super(EMPTY_CONTAINER, index, x, y);
		this.handler = handler;
		this.index = index;
	}

	public IItemHandler getItemHandler() {
		return handler;
	}

	@NotNull
	@Override
	public ItemStack getItem() {
		return handler.getStackInSlot(index);
	}

	@Override
	public void set(@NotNull ItemStack stack) {
		if (handler instanceof IItemHandlerModifiable modifiable) {
			modifiable.setStackInSlot(index, stack);
		}
	}

	@Override
	public boolean mayPlace(@NotNull ItemStack stack) {
		return !stack.isEmpty() && handler.isItemValid(index, stack);
	}

	@Override
	public int getMaxStackSize() {
		return handler.getSlotLimit(index);
	}

	@Override
	public int getMaxStackSize(@NotNull ItemStack stack) {
		return Math.min(getMaxStackSize(), stack.getMaxStackSize());
	}

	@NotNull
	@Override
	public ItemStack remove(int amount) {
		return handler.extractItem(index, amount, false);
	}

	@Override
	public void setChanged() {
		// No-op: the handler manages its own persistence
	}

	@Override
	public boolean hasItem() {
		return !getItem().isEmpty();
	}
}
