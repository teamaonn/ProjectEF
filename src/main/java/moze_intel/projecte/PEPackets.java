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

    public record Snapshot(boolean tablet, long position, long balance, List<String> learned, Map<String, Long> prices, List<net.minecraft.world.item.ItemStack> variants, List<String> favorites, int knowledgeRevision) implements CustomPacketPayload {
        public static final Type<Snapshot> TYPE = new Type<>(PECore.id("snapshot"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Snapshot> CODEC = CustomPacketPayload.codec(
                (packet, buf) -> {
                    buf.writeBoolean(packet.tablet); buf.writeLong(packet.position);
                    buf.writeLong(packet.balance); buf.writeVarInt(packet.learned.size());
                    packet.learned.forEach(value -> buf.writeUtf(value, 256));
                    buf.writeVarInt(packet.prices.size());
                    packet.prices.forEach((id, value) -> { buf.writeUtf(id); buf.writeLong(value); });
                    buf.writeVarInt(packet.variants.size());
                    packet.variants.forEach(stack -> net.minecraft.world.item.ItemStack.STREAM_CODEC.encode(buf, stack));
                    buf.writeVarInt(packet.favorites.size()); packet.favorites.forEach(key -> buf.writeUtf(key, 256));
                    buf.writeVarInt(packet.knowledgeRevision);
                }, buf -> {
                    boolean tablet = buf.readBoolean(); long position = buf.readLong(); long balance = buf.readLong();
                    int learnedSize = buf.readVarInt();
                    if (learnedSize < 0 || learnedSize > 4096) throw new IllegalArgumentException("Too many learned items");
                    List<String> learned = new java.util.ArrayList<>(learnedSize);
                    for (int i = 0; i < learnedSize; i++) learned.add(buf.readUtf(256));
                    Map<String, Long> prices = new LinkedHashMap<>();
                    int size = buf.readVarInt();
                    if (size < 0 || size > 4096) throw new IllegalArgumentException("Too many EMC overrides");
                    for (int i = 0; i < size; i++) prices.put(buf.readUtf(256), buf.readLong());
                    int variantSize = buf.readVarInt();
                    if (variantSize < 0 || variantSize > 4096) throw new IllegalArgumentException("Too many variants");
                    List<net.minecraft.world.item.ItemStack> variants = new java.util.ArrayList<>(variantSize);
                    for (int i = 0; i < variantSize; i++) variants.add(net.minecraft.world.item.ItemStack.STREAM_CODEC.decode(buf));
                    int favoriteSize = buf.readVarInt();
                    if (favoriteSize < 0 || favoriteSize > 64) throw new IllegalArgumentException("Too many favorites");
                    List<String> favorites = new java.util.ArrayList<>();
                    for (int i = 0; i < favoriteSize; i++) favorites.add(buf.readUtf(256));
                    return new Snapshot(tablet, position, balance, learned, prices, variants, favorites, buf.readVarInt());
                });
        @Override public Type<Snapshot> type() { return TYPE; }
    }

    public record StoneAction(int command, boolean offhand) implements CustomPacketPayload {
        public static final Type<StoneAction> TYPE = new Type<>(PECore.id("stone_action"));
        public static final StreamCodec<RegistryFriendlyByteBuf, StoneAction> CODEC = CustomPacketPayload.codec(
            (packet, buf) -> {buf.writeVarInt(packet.command);buf.writeBoolean(packet.offhand);},
            buf -> new StoneAction(buf.readVarInt(),buf.readBoolean()));
        @Override public Type<StoneAction> type(){return TYPE;}
    }

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(StoneAction.TYPE, StoneAction.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(Action.TYPE, Action.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(Snapshot.TYPE, Snapshot.CODEC);
    }
}
