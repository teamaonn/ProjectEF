package moze_intel.projecte;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class MatterFurnaceBlock extends FurnaceBlock {
    private final boolean redMatter;

    public MatterFurnaceBlock(boolean redMatter, Properties properties) {
        super(properties);
        this.redMatter = redMatter;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MatterFurnaceBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof MenuProvider provider) player.openMenu(provider);
        return InteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : (lvl, pos, blockState, entity) -> {
            if (lvl instanceof ServerLevel serverLevel && entity instanceof MatterFurnaceBlockEntity furnace) {
                AbstractFurnaceBlockEntity.serverTick(serverLevel, pos, blockState, furnace);
            }
        };
    }
}
