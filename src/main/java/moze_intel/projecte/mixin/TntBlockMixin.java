package moze_intel.projecte.mixin;

import moze_intel.projecte.gameObjs.blocks.ProjectETNT;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric has no {@code TntBlock.onCaughtFire} hook, so vanilla ignition paths that are not virtual
 * methods on the block call the static {@link TntBlock#explode} directly and spawn a plain vanilla
 * {@code PrimedTnt} instead of the custom ProjectEF entity. The most notable one is the vanilla
 * flint and steel dispenser behavior ({@code DispenseItemBehavior$8}), which calls explode
 * <em>before</em> removing the block.
 *
 * <p>Every such caller still has the block present at that point, so this catches them generically
 * (including third party callers) by checking the block at the position. The fire spread path in
 * {@code FireBlock.checkBurnOut} removes the block <em>before</em> calling explode, so the state is
 * already gone by then and it stays handled by {@link FireBlockMixin} instead; its fallback call to
 * {@link TntBlock#explode} reads back air here and correctly falls through to vanilla behavior.
 */
@Mixin(TntBlock.class)
public abstract class TntBlockMixin {

	@Inject(method = "explode(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", at = @At("HEAD"), cancellable = true)
	private static void projecte$explodeCustomTnt(Level level, BlockPos pos, CallbackInfo ci) {
		if (level.getBlockState(pos).getBlock() instanceof ProjectETNT tnt) {
			if (!level.isClientSide) {
				tnt.createAndAddEntity(level, pos, null);
			}
			ci.cancel();
		}
	}
}
