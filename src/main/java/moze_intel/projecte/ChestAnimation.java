package moze_intel.projecte;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;

final class ChestAnimation {
    private static final int LID_EVENT = 1;
    private final BlockEntity blockEntity;
    private final ChestLidController lid = new ChestLidController();
    private final ContainerOpenersCounter openers;

    ChestAnimation(BlockEntity blockEntity, Predicate<Player> hasMenuOpen) {
        this.blockEntity = blockEntity;
        openers = new ContainerOpenersCounter() {
            @Override protected void onOpen(Level level, BlockPos pos, BlockState state) {}
            @Override protected void onClose(Level level, BlockPos pos, BlockState state) {}

            @Override
            protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int oldCount, int newCount) {
                level.blockEvent(pos, state.getBlock(), LID_EVENT, newCount > 0 ? 1 : 0);
            }

            @Override
            public boolean isOwnContainer(Player player) {
                return !player.isSpectator() && hasMenuOpen.test(player);
            }
        };
    }

    void startOpen(ContainerUser user) {
        if (blockEntity.getLevel() instanceof ServerLevel level
                && !blockEntity.isRemoved() && !user.getLivingEntity().isSpectator()) {
            openers.incrementOpeners(user.getLivingEntity(), level, blockEntity.getBlockPos(),
                    blockEntity.getBlockState(), user.getContainerInteractionRange());
        }
    }

    void stopOpen(ContainerUser user) {
        if (blockEntity.getLevel() instanceof ServerLevel level && !blockEntity.isRemoved()
                && !user.getLivingEntity().isSpectator() && openers.getOpenerCount() > 0) {
            openers.decrementOpeners(user.getLivingEntity(), level, blockEntity.getBlockPos(), blockEntity.getBlockState());
        }
    }

    void recheckOpeners(ServerLevel level, BlockPos pos, BlockState state) {
        openers.recheckOpeners(level, pos, state);
    }

    boolean handleEvent(int id, int open) {
        if (id != LID_EVENT) return false;
        lid.shouldBeOpen(open > 0);
        return true;
    }

    void tickLid() {
        lid.tickLid();
    }

    float openness(float partialTick) {
        return lid.getOpenness(partialTick);
    }
}
