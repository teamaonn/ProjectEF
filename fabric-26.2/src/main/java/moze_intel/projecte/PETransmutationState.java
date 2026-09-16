package moze_intel.projecte;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class PETransmutationState extends SavedData {
    private static final Map<Identifier, Long> VALUES = new LinkedHashMap<>();
    static {
        try (InputStream stream = PETransmutationState.class.getClassLoader()
                .getResourceAsStream("data/projecte/emc_values.json")) {
            if (stream == null) throw new IllegalStateException("Missing projecte EMC values");
            var entries = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                    .getAsJsonObject();
            entries.entrySet().forEach(entry -> {
                long amount = entry.getValue().getAsLong();
                if (amount > 0) price(entry.getKey(), amount);
            });
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Unable to read projecte EMC values", exception);
        }
    }

    private record Account(long balance, List<String> learned) {
        static final Codec<Account> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.LONG.fieldOf("balance").forGetter(Account::balance),
                Codec.STRING.listOf().fieldOf("learned").forGetter(Account::learned)
        ).apply(instance, Account::new));
    }
    private static final Codec<PETransmutationState> CODEC = Codec.unboundedMap(Codec.STRING, Account.CODEC)
            .xmap(PETransmutationState::new, state -> state.accounts);
    public static final SavedDataType<PETransmutationState> TYPE = new SavedDataType<>(
            PECore.id("transmutation_accounts"), PETransmutationState::new, CODEC, null);
    private final Map<String, Account> accounts;
    private PETransmutationState() { accounts = new LinkedHashMap<>(); }
    private PETransmutationState(Map<String, Account> accounts) { this.accounts = new LinkedHashMap<>(accounts); }

    private static void price(String id, long value) { VALUES.put(Identifier.parse(id), value); }
    public static long value(String id) {
        try { return VALUES.getOrDefault(Identifier.parse(id), 0L); }
        catch (IllegalArgumentException exception) { return 0; }
    }
    private static PETransmutationState state(ServerPlayer player) {
        return player.level().getServer().overworld().getDataStorage().computeIfAbsent(TYPE);
    }
    private Account account(ServerPlayer player) {
        return accounts.getOrDefault(player.getUUID().toString(), new Account(0, List.of()));
    }
    private void update(ServerPlayer player, Account account) {
        accounts.put(player.getUUID().toString(), account);
        setDirty();
    }
    public static boolean access(ServerPlayer player, boolean tablet, long position) {
        if (tablet) return PERegistries.isTransmutationTablet(player.getMainHandItem().getItem())
                || PERegistries.isTransmutationTablet(player.getOffhandItem().getItem());
        BlockPos pos = BlockPos.of(position);
        return player.distanceToSqr(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) <= 64
                && player.level().isLoaded(pos)
                && PERegistries.isTransmutationTable(player.level().getBlockState(pos).getBlock());
    }
    public static void open(ServerPlayer player, boolean tablet, long position) {
        if (access(player, tablet, position)) sync(player, tablet, position);
    }
    public static void action(ServerPlayer player, PEPackets.Action request) {
        if (!access(player, request.tablet(), request.position())) return;
        PETransmutationState state = state(player);
        Account current = state.account(player);
        if ("sell".equals(request.command())) {
            int slot = request.slot();
            if (slot < 0 || slot >= player.getInventory().getContainerSize()) return;
            ItemStack stack = player.getInventory().getItem(slot);
            String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            long unit = value(id);
            int quantity = Math.min(stack.getCount(), Math.max(1, Math.min(request.count(), 64)));
            if (unit > 0 && quantity > 0 && unit <= (Long.MAX_VALUE - current.balance()) / quantity) {
                Set<String> known = new LinkedHashSet<>(current.learned()); known.add(id);
                state.update(player, new Account(current.balance() + unit * quantity, List.copyOf(known)));
                stack.shrink(quantity);
            }
        } else if ("buy".equals(request.command())) {
            String id = request.item();
            long unit = value(id);
            int quantity = Math.max(1, Math.min(request.count(), 64));
            if (unit > 0 && current.learned().contains(id) && unit <= current.balance() / quantity) {
                Item item;
                try { item = BuiltInRegistries.ITEM.getValue(Identifier.parse(id)); }
                catch (IllegalArgumentException exception) { return; }
                if (item == Items.AIR) return;
                ItemStack stack = new ItemStack(item, Math.min(quantity, item.getDefaultMaxStackSize()));
                if (stack.getCount() != quantity) return;
                state.update(player, new Account(current.balance() - unit * quantity, current.learned()));
                if (!player.getInventory().add(stack)) player.drop(stack, false);
            }
        }
        sync(player, request.tablet(), request.position());
    }
    private static void sync(ServerPlayer player, boolean tablet, long position) {
        Account account = state(player).account(player);
        if (ServerPlayNetworking.canSend(player, PEPackets.Snapshot.TYPE))
            ServerPlayNetworking.send(player, new PEPackets.Snapshot(tablet, position, account.balance(), account.learned()));
    }
}
