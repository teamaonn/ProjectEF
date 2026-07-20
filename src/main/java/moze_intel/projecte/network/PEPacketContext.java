package moze_intel.projecte.network;

import net.minecraft.world.entity.player.Player;

/**
 * Loader independent context handed to packet handlers. On the server the player is the sending {@link net.minecraft.server.level.ServerPlayer}, on the client it
 * is the local client player.
 */
@FunctionalInterface
public interface PEPacketContext {

	Player player();
}
