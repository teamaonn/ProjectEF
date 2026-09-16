package moze_intel.projecte;

import java.util.List;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public final class PEPackets {
    private PEPackets() {}

    // source: tablet = true; table position is supplied only for a placed table.
    public record Action(boolean tablet, long position, String command, int slot, String item, int count) implements CustomPacketPayload {
        public static final Type<Action> TYPE = new Type<>(PECore.id("action"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Action> CODEC = CustomPacketPayload.codec(
                (packet, buf) -> {
                    buf.writeBoolean(packet.tablet); buf.writeLong(packet.position);
                    buf.writeUtf(packet.command); buf.writeVarInt(packet.slot);
                    buf.writeUtf(packet.item); buf.writeVarInt(packet.count);
                }, buf -> new Action(buf.readBoolean(), buf.readLong(), buf.readUtf(16), buf.readVarInt(), buf.readUtf(256), buf.readVarInt()));
        @Override public Type<Action> type() { return TYPE; }
    }

    public record Snapshot(boolean tablet, long position, long balance, List<String> learned) implements CustomPacketPayload {
        public static final Type<Snapshot> TYPE = new Type<>(PECore.id("snapshot"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Snapshot> CODEC = CustomPacketPayload.codec(
                (packet, buf) -> {
                    buf.writeBoolean(packet.tablet); buf.writeLong(packet.position);
                    buf.writeLong(packet.balance); buf.writeCollection(packet.learned, (b, value) -> b.writeUtf(value));
                }, buf -> new Snapshot(buf.readBoolean(), buf.readLong(), buf.readLong(), buf.readList(
                        b -> b.readUtf(256))));
        @Override public Type<Snapshot> type() { return TYPE; }
    }

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(Action.TYPE, Action.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(Snapshot.TYPE, Snapshot.CODEC);
    }
}
