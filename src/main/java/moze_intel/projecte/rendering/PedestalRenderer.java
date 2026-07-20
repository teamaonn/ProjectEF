package moze_intel.projecte.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import moze_intel.projecte.gameObjs.block_entities.DMPedestalBlockEntity;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class PedestalRenderer implements BlockEntityRenderer<DMPedestalBlockEntity> {

	private final BlockEntityRendererProvider.Context context;

	public PedestalRenderer(BlockEntityRendererProvider.Context context) {
		this.context = context;
	}

	@Override
	public void render(DMPedestalBlockEntity pedestal, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		net.minecraft.world.item.ItemStack stack = pedestal.getInventory().getStackInSlot(0);
		if (stack.isEmpty()) {
			return;
		}
		poseStack.pushPose();
		poseStack.translate(0.5, 1.0, 0.5);
		long time = pedestal.getLevel() != null ? pedestal.getLevel().getGameTime() : 0;
		float angle = (time % 80) / 80.0F * 360.0F;
		poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(angle));
		poseStack.scale(0.6F, 0.6F, 0.6F);
		var mc = net.minecraft.client.Minecraft.getInstance();
		mc.getItemRenderer().renderStatic(stack, net.minecraft.world.item.ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, pedestal.getLevel(), 0);
		poseStack.popPose();
	}
}