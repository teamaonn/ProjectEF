package moze_intel.projecte.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.PhilosophersStone;
import moze_intel.projecte.gameObjs.items.PhilosophersStone.PhilosophersStoneMode;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.Constants;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TransmutationRenderingOverlay {

	private final Minecraft mc = Minecraft.getInstance();
	@Nullable
	private Block transmutationResult;
	private long lastGameTime;

	/**
	 * Renders the little preview of what the philosopher's stone will transmute the targeted block into, hooked into the hud render callback.
	 */
	public void onHudRender(@NotNull GuiGraphics graphics, @NotNull DeltaTracker delta) {
		if (!mc.options.hideGui && transmutationResult != null) {
			if (transmutationResult instanceof LiquidBlock liquidBlock) {
				FluidState fluidState = liquidBlock.fluid.defaultFluidState();
				FluidRenderHandler renderHandler = FluidRenderHandlerRegistry.INSTANCE.get(liquidBlock.fluid);
				if (renderHandler != null) {
					int color = renderHandler.getFluidColor(null, null, fluidState);
					float red = (color >> 16 & 0xFF) / 255.0F;
					float green = (color >> 8 & 0xFF) / 255.0F;
					float blue = (color & 0xFF) / 255.0F;
					TextureAtlasSprite sprite = renderHandler.getFluidSprites(null, null, fluidState)[0];
					graphics.blit(1, 1, 0, 16, 16, sprite, red, green, blue, 1);
				}
			} else {
				//Just render it normally instead of with the given model as some block's don't render properly then as an item
				// for example glass panes
				graphics.renderItem(new ItemStack(transmutationResult), 1, 1);
			}
			long gameTime = mc.level == null ? 0 : mc.level.getGameTime();
			if (lastGameTime != gameTime) {
				//If the game time changed, so we aren't actually still hovering a block set our
				// result to null. We do this after rendering it just in case there is a single
				// frame where this may actually be valid based on the order the events are fired
				transmutationResult = null;
				lastGameTime = gameTime;
			}
		}
	}

	/**
	 * Highlights the blocks the philosopher's stone would change and remembers the result for the hud preview, hooked into the block outline render event.
	 *
	 * @return always true so the vanilla outline still renders.
	 */
	public boolean onBlockOutline(WorldRenderContext context, WorldRenderContext.BlockOutlineContext outlineContext) {
		if (!(outlineContext.entity() instanceof Player player)) {
			return true;
		}
		Level level = player.level();
		lastGameTime = level.getGameTime();
		ItemStack stack = player.getMainHandItem();
		if (stack.isEmpty()) {
			stack = player.getOffhandItem();
		}
		if (stack.isEmpty() || !stack.is(PEItems.PHILOSOPHERS_STONE)) {
			transmutationResult = null;
			return true;
		}
		boolean isSneaking = player.isSecondaryUseActive();
		PhilosophersStone philoStone = (PhilosophersStone) stack.getItem();
		//Note: We use the philo stone's ray trace instead of the outline's position as we want to make sure that we
		// can properly take fluid into account/ignore it when needed
		BlockHitResult rtr = philoStone.getHitBlock(level, player, isSneaking);
		if (rtr.getType() == HitResult.Type.BLOCK) {
			int charge = philoStone.getCharge(stack);
			PhilosophersStoneMode mode = philoStone.getMode(stack);
			Object2ReferenceMap<BlockPos, BlockState> changes = PhilosophersStone.getChanges(level, rtr.getBlockPos(), rtr.getDirection(), player.getDirection(),
					isSneaking, mode, charge);
			if (changes.isEmpty()) {
				transmutationResult = null;
			} else {
				transmutationResult = changes.values().iterator().next().getBlock();
				Vec3 viewPosition = context.camera().getPosition();
				float alpha = ProjectEConfig.client.pulsatingOverlay.get() ? getPulseProportion() * 0.60F : 0.35F;
				MultiBufferSource consumers = context.consumers();
				if (consumers == null) {
					return true;
				}
				VertexConsumer builder = consumers.getBuffer(PERenderType.TRANSMUTATION_OVERLAY);
				PoseStack matrix = context.matrixStack();
				if (matrix == null) {
					return true;
				}
				CollisionContext selectionContext = CollisionContext.of(player);
				for (BlockPos pos : changes.keySet()) {
					BlockState state = level.getBlockState(pos);
					if (!state.isAir()) {
						VoxelShape shape = state.getShape(level, pos, selectionContext);
						if (!shape.isEmpty()) {
							matrix.pushPose();
							//Shift by view position here so that we don't have floating point issues at large values
							matrix.translate(pos.getX() - viewPosition.x, pos.getY() - viewPosition.y, pos.getZ() - viewPosition.z);
							shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
								for (Direction value : Constants.DIRECTIONS) {
									LevelRenderer.renderFace(matrix, builder, value, (float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, 1, 1, 1, alpha);
								}
							});
							matrix.popPose();
						}
					}
				}
			}
		} else {
			transmutationResult = null;
		}
		return true;
	}

	private float getPulseProportion() {
		return (float) (0.5F * Math.sin(System.currentTimeMillis() / 350.0) + 0.5F);
	}
}
