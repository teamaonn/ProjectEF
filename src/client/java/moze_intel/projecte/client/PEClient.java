package moze_intel.projecte.client;

import moze_intel.projecte.PEPackets;
import moze_intel.projecte.PERegistries;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.ItemEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionResult;

public class PEClient implements ClientModInitializer {
    @Override public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(PEPackets.Snapshot.TYPE, (packet, context) ->
                context.client().execute(() ->
                        context.client().gui.setScreen(new TransmutationScreen(packet))));
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (!level.isClientSide() || !PERegistries.isTransmutationTable(
                    level.getBlockState(hit.getBlockPos()).getBlock())) return InteractionResult.PASS;
            ClientPlayNetworking.send(new PEPackets.Action(false, hit.getBlockPos().asLong(), "open", 0, "", 1));
            return InteractionResult.SUCCESS;
        });
        ItemEvents.USE.register((level, player, hand) -> {
            if (!level.isClientSide() || !PERegistries.isTransmutationTablet(player.getItemInHand(hand).getItem())) return null;
            ClientPlayNetworking.send(new PEPackets.Action(true, 0, "open", 0, "", 1));
            return InteractionResult.SUCCESS;
        });
    }
}
