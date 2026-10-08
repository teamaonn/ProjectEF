package moze_intel.projecte;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/** Matches ProjectEF's /projecte setemc <emc> [item] syntax. */
public final class PESetEmcCommand {
    private PESetEmcCommand() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) ->
                dispatcher.register(Commands.literal("projecte")
                        .then(Commands.literal("setemc")
                                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR))
                                .then(Commands.argument("emc", LongArgumentType.longArg(0))
                                        .executes(context -> {
                                            var player = context.getSource().getPlayerOrException();
                                            if (player.getMainHandItem().isEmpty()) {
                                                context.getSource().sendFailure(Component.literal("Hold an item or provide an item ID."));
                                                return 0;
                                            }
                                            String id = BuiltInRegistries.ITEM.getKey(player.getMainHandItem().getItem()).toString();
                                            return set(context.getSource(), id, LongArgumentType.getLong(context, "emc"));
                                        })
                                        .then(Commands.argument("item", StringArgumentType.word())
                                                .executes(context -> set(context.getSource(),
                                                        StringArgumentType.getString(context, "item"),
                                                        LongArgumentType.getLong(context, "emc"))))))));
    }

    private static int set(net.minecraft.commands.CommandSourceStack source, String id, long emc) {
        Identifier key;
        try { key = Identifier.parse(id); }
        catch (IllegalArgumentException invalid) {
            source.sendFailure(Component.literal("Invalid item ID: " + id));
            return 0;
        }
        Item item = BuiltInRegistries.ITEM.getValue(key);
        if (item == Items.AIR || !BuiltInRegistries.ITEM.getKey(item).equals(key)) {
            source.sendFailure(Component.literal("Unknown item: " + id));
            return 0;
        }
        PEEmcOverrides.forServer(source.getServer()).set(key.toString(), emc);
        source.sendSuccess(() -> Component.literal("Set " + key + " to " + emc + " EMC. Reopen the table to refresh its prices."), false);
        return 1;
    }
}
