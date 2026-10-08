package moze_intel.projecte;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

public final class MatterFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    public MatterFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(PERegistries.MATTER_FURNACE_ENTITY, pos, state, RecipeType.SMELTING);
    }

    @Override
    protected Component getDefaultName() {
        return Component.literal("Matter Furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new FurnaceMenu(syncId, playerInventory, this, this.dataAccess);
    }
}
