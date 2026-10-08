package moze_intel.projecte;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class MatterFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    private final boolean redMatter;

    public MatterFurnaceBlockEntity(BlockPos pos, BlockState state, boolean redMatter) {
        super(PERegistries.MATTER_FURNACE_ENTITY, pos, state, RecipeType.SMELTING);
        this.redMatter = redMatter;
    }

    @Override
    protected Component getDefaultName() {
        return Component.literal(redMatter ? "Red Matter Furnace" : "Dark Matter Furnace");
    }

    @Override
    protected float getSpeedMultiplier(ServerLevel level, ItemStack fuelItem) {
        return redMatter ? (200.0F / 3.0F) : 20.0F;
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new FurnaceMenu(syncId, playerInventory, this, this.dataAccess);
    }
}
