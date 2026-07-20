package moze_intel.projecte.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/**
 * Small helpers around the fabric networking api, mirroring the send methods the old PacketDistributor used to provide.
 */
public final class PENetwork {

	private PENetwork() {
	}

	public static void sendToPlayer(ServerPlayer player, CustomPacketPayload... payloads) {
		for (CustomPacketPayload payload : payloads) {
			ServerPlayNetworking.send(player, payload);
		}
	}

	/**
	 * May only be called from client code.
	 */
	public static void sendToServer(CustomPacketPayload payload) {
		ClientSender.send(payload);
	}

	/**
	 * Nested class so that client only classes are never touched on a dedicated server (classes are only loaded on first access).
	 */
	private static class ClientSender {

		private static void send(CustomPacketPayload payload) {
			ClientPlayNetworking.send(payload);
		}
	}
}
