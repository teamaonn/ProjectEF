package moze_intel.projecte;

import com.mojang.serialization.Codec;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/** World-specific, operator-set EMC prices. A zero price disables an item. */
public final class PEEmcOverrides extends SavedData {
    private static final Codec<PEEmcOverrides> CODEC = Codec.unboundedMap(Codec.STRING, Codec.LONG)
            .xmap(PEEmcOverrides::new, overrides -> overrides.prices);
    public static final SavedDataType<PEEmcOverrides> TYPE = new SavedDataType<>(
            PECore.id("emc_overrides"), PEEmcOverrides::new, CODEC, null);
    private final Map<String, Long> prices;

    private PEEmcOverrides() { prices = new LinkedHashMap<>(); }
    private PEEmcOverrides(Map<String, Long> prices) { this.prices = new LinkedHashMap<>(prices); }

    public static PEEmcOverrides forPlayer(ServerPlayer player) {
        return forServer(player.level().getServer());
    }
    public static PEEmcOverrides forServer(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }
    public long value(String item) {
        return prices.getOrDefault(item, PETransmutationState.value(item));
    }
    public Map<String, Long> snapshot() { return Map.copyOf(prices); }
    public void set(String item, long amount) {
        prices.put(item, amount);
        setDirty();
    }
}
