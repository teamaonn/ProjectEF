package moze_intel.projecte.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import moze_intel.projecte.PERegistries;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.SimpleUnbakedExtraModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

final class ChestLidRenderer<T extends BlockEntity & LidBlockEntity>
        implements BlockEntityRenderer<T, ChestLidRenderer.State> {
    private static final int[] NO_TINTS = {};
    private static final float HINGE_HEIGHT = 10 / 16F;
    private static final float HINGE_DEPTH = 15 / 16F;
    private final ExtraModelKey<List<BlockStateModelPart>> modelKey;

    private ChestLidRenderer(ExtraModelKey<List<BlockStateModelPart>> modelKey) {
        this.modelKey = modelKey;
    }

    static void register() {
        registerLid(PERegistries.ALCHEMICAL_CHEST_ENTITY, "alchemical_chest");
        registerLid(PERegistries.ENERGY_CONDENSER_ENTITY, "energy_condenser");
    }

    private static <T extends BlockEntity & LidBlockEntity> void registerLid(BlockEntityType<T> type, String name) {
        Identifier id = Identifier.fromNamespaceAndPath("projecte", "block/" + name + "_lid");
        ExtraModelKey<List<BlockStateModelPart>> key = ExtraModelKey.create(id::toString);
        ModelLoadingPlugin.register(context -> context.addModel(key,
                new SimpleUnbakedExtraModel<>(id, (model, baker) -> List.of(new Variant(id).bake(baker)))));
        BlockEntityRenderers.register(type, context -> new ChestLidRenderer<>(key));
    }

    static final class State extends BlockEntityRenderState {
        List<BlockStateModelPart> parts;
        float facingRotation;
        float openingAngle;
    }

    @Override
    public boolean shouldRender(T chest, Vec3 cameraPos) {
        // Use the same section visibility as the chest body.
        return true;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(T chest, State state, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(chest, state, partialTick, cameraPos, overlay);
        state.parts = Minecraft.getInstance().getModelManager().getModel(modelKey);
        state.facingRotation = 180 - chest.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot();
        float closed = 1 - chest.getOpenNess(partialTick);
        state.openingAngle = 90 * (1 - closed * closed * closed);
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector nodes, CameraRenderState camera) {
        pose.pushPose();
        pose.translate(0.5, 0, 0.5);
        pose.rotateDegrees(Axis.YP, state.facingRotation);
        pose.translate(-0.5, 0, -0.5);
        pose.translate(0, HINGE_HEIGHT, HINGE_DEPTH);
        pose.rotateDegrees(Axis.XP, state.openingAngle);
        pose.translate(0, -HINGE_HEIGHT, -HINGE_DEPTH);
        nodes.submitBlockModel(pose, RenderTypes.solidMovingBlock(), state.parts,
                NO_TINTS, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        if (state.breakProgress != null) {
            nodes.order(1).submitBreakingBlockModel(pose, state.parts, state.breakProgress.progress(), false);
        }
        pose.popPose();
    }
}
