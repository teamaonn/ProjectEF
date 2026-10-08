package moze_intel.projecte;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;

/**
 * A ProjectE matter furnace shell using the vanilla furnace inventory and
 * cooking implementation.  The accelerated/EMC-powered behavior can then be
 * layered on without making the block uncraftable or nonfunctional.
 */
public final class MatterFurnaceBlock extends FurnaceBlock {
    public MatterFurnaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FurnaceBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : (lvl, pos, blockState, entity) -> {
            if (lvl instanceof ServerLevel serverLevel && entity instanceof FurnaceBlockEntity furnace) {
                FurnaceBlockEntity.serverTick(serverLevel, pos, blockState, furnace);
            }
        };
    }
}
