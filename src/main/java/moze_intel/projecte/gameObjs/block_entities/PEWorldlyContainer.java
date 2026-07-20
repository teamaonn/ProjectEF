package moze_intel.projecte.gameObjs.block_entities;

import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import moze_intel.projecte.api.item_handlers.IItemHandler;
import moze_intel.projecte.api.item_handlers.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for ProjectE block entities that want to work with vanilla hoppers and modded pipes.
 * Implementations should delegate to their existing IItemHandler-based side inventory views.
 */
public interface PEWorldlyContainer extends WorldlyContainer {

    /** Returns the full inventory handler (all slots combined). */
    IItemHandler getFullHandler();

    /** Returns the side-specific handler view for the given face. */
    IItemHandler getSideHandler(@Nullable Direction side);

    @Override
    default int getContainerSize() {
        return getFullHandler().getSlots();
    }

    @Override
    default boolean isEmpty() {
        IItemHandler handler = getFullHandler();
        for (int i = 0, slots = handler.getSlots(); i < slots; i++) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    default net.minecraft.world.item.ItemStack getItem(int slot) {
        return getFullHandler().getStackInSlot(slot);
    }

    @Override
    default net.minecraft.world.item.ItemStack removeItem(int slot, int amount) {
        return getFullHandler().extractItem(slot, amount, false);
    }

    @Override
    default net.minecraft.world.item.ItemStack removeItemNoUpdate(int slot) {
        net.minecraft.world.item.ItemStack stack = getFullHandler().getStackInSlot(slot).copy();
        if (stack.isEmpty()) {
            return net.minecraft.world.item.ItemStack.EMPTY;
        }
        if (getFullHandler() instanceof IItemHandlerModifiable modifiable) {
            modifiable.setStackInSlot(slot, net.minecraft.world.item.ItemStack.EMPTY);
        }
        return stack;
    }

    @Override
    default void setItem(int slot, net.minecraft.world.item.ItemStack stack) {
        if (getFullHandler() instanceof IItemHandlerModifiable modifiable) {
            modifiable.setStackInSlot(slot, stack);
        }
    }

    @Override
    default int getMaxStackSize() {
        return 64;
    }


    @Override
    default boolean stillValid(Player player) {
        return true;
    }

    @Override
    default int @NotNull [] getSlotsForFace(Direction side) {
        IItemHandler handler = getSideHandler(side);
        int[] slots = new int[handler.getSlots()];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = i;
        }
        return slots;
    }

    @Override
	default void clearContent() {
		IItemHandler handler = getFullHandler();
		if (handler instanceof IItemHandlerModifiable modifiable) {
			for (int i = 0, slots = handler.getSlots(); i < slots; i++) {
				modifiable.setStackInSlot(i, net.minecraft.world.item.ItemStack.EMPTY);
			}
		}
	}

	default boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, @Nullable Direction side) {
        IItemHandler handler = getSideHandler(side);
        return slot >= 0 && slot < handler.getSlots() && handler.insertItem(slot, stack, true).getCount() < stack.getCount();
    }

    @Override
    default boolean canTakeItemThroughFace(int slot, net.minecraft.world.item.ItemStack stack, Direction side) {
        IItemHandler handler = getSideHandler(side);
        return slot >= 0 && slot < handler.getSlots() && !handler.extractItem(slot, stack.getCount(), true).isEmpty();
    }
}
