package moze_intel.projecte.gameObjs.container.slots;

import moze_intel.projecte.api.item_handlers.IItemHandler;
import moze_intel.projecte.api.item_handlers.IItemHandlerModifiable;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Clean-room implementation of NeoForge's ItemHandlerCopySlot.
 * <p>
 * A slot that wraps an {@link IItemHandlerModifiable} and provides copy-on-read semantics
 * to prevent data mutation issues with component-based item handlers.
 */
public class ItemHandlerCopySlot extends SlotItemHandler {

	public ItemHandlerCopySlot(IItemHandler handler, int slotIndex, int xPosition, int yPosition) {
		super(handler, slotIndex, xPosition, yPosition);
	}

	@NotNull
	@Override
	public ItemStack getItem() {
		return handler.getStackInSlot(index).copy();
	}

	@Override
	public void set(@NotNull ItemStack stack) {
		((IItemHandlerModifiable) handler).setStackInSlot(index, stack.copy());
	}
}
