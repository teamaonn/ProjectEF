package moze_intel.projecte;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
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

    public record Snapshot(boolean tablet, long position, long balance, List<String> learned, Map<String, Long> prices) implements CustomPacketPayload {
        public static final Type<Snapshot> TYPE = new Type<>(PECore.id("snapshot"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Snapshot> CODEC = CustomPacketPayload.codec(
                (packet, buf) -> {
                    buf.writeBoolean(packet.tablet); buf.writeLong(packet.position);
                    buf.writeLong(packet.balance); buf.writeCollection(packet.learned, (b, value) -> b.writeUtf(value));
                    buf.writeVarInt(packet.prices.size());
                    packet.prices.forEach((id, value) -> { buf.writeUtf(id); buf.writeLong(value); });
                }, buf -> {
                    boolean tablet = buf.readBoolean(); long position = buf.readLong(); long balance = buf.readLong();
                    List<String> learned = buf.readList(b -> b.readUtf(256));
                    Map<String, Long> prices = new LinkedHashMap<>();
                    int size = buf.readVarInt();
                    if (size < 0 || size > 4096) throw new IllegalArgumentException("Too many EMC overrides");
                    for (int i = 0; i < size; i++) prices.put(buf.readUtf(256), buf.readLong());
                    return new Snapshot(tablet, position, balance, learned, prices);
                });
        @Override public Type<Snapshot> type() { return TYPE; }
    }

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(Action.TYPE, Action.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(Snapshot.TYPE, Snapshot.CODEC);
    }
}
