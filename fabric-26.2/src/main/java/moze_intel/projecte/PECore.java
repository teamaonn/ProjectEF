package moze_intel.projecte;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.UUID;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.ItemEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import org.slf4j.Logger;

public class PECore implements ModInitializer {

    public static final String MODID = "projecte";
    public static final String MODNAME = "ProjectE";
    public static final GameProfile FAKEPLAYER_GAMEPROFILE = new GameProfile(UUID.fromString("590e39c7-9fb6-471b-a4c2-c0e539b2423d"), "[" + MODNAME + "]");
    public static final Logger LOGGER = LogUtils.getLogger();

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    @Override
    public void onInitialize() {
        PERegistries.register();
        PEPackets.register();
        PESetEmcCommand.register();
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(
                PEPackets.Action.TYPE, (packet, context) ->
                        context.server().execute(() -> {
                            if ("open".equals(packet.command()))
                                PETransmutationState.open(context.player(), packet.tablet(), packet.position());
                            else PETransmutationState.action(context.player(), packet);
                        }));
        LOGGER.info("{} Fabric port shell initialized", MODNAME);
    }
}
