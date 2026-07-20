package moze_intel.projecte.impl;

import java.util.Objects;
import java.util.UUID;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.proxy.ITransmutationProxy;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class TransmutationProxyImpl implements ITransmutationProxy {

	@NotNull
	@Override
	public IKnowledgeProvider getKnowledgeProviderFor(@NotNull UUID playerUUID) {
		MinecraftServer server = PECore.getServer();
		if (server != null) {
			Objects.requireNonNull(playerUUID);
			Player player = server.getPlayerList().getPlayer(playerUUID);
			if (player != null) {
				return Objects.requireNonNull(PECapabilities.KNOWLEDGE_CAPABILITY.find(player));
			}
			return TransmutationOffline.forPlayer(server, playerUUID);
		} else if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			return ClientHelper.getKnowledgeProvider();
		}
		throw new IllegalStateException("unreachable");
	}

	/**
	 * Client-side helper to avoid direct references to client-only classes (Minecraft) in common code.
	 */
	private static class ClientHelper {

		@NotNull
		public static IKnowledgeProvider getKnowledgeProvider() {
			Player player = net.minecraft.client.Minecraft.getInstance().player;
			Objects.requireNonNull(player, "Client player doesn't exist!");
			return Objects.requireNonNull(PECapabilities.KNOWLEDGE_CAPABILITY.find(player));
		}
	}
}