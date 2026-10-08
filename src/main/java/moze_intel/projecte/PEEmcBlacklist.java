package moze_intel.projecte;

import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

/** Items that exist in the registry but cannot be obtained through normal survival play. */
public final class PEEmcBlacklist {
    private static final Set<String> EXACT = Set.of(
            "minecraft:air", "minecraft:barrier", "minecraft:bedrock", "minecraft:budding_amethyst",
            "minecraft:chain_command_block", "minecraft:command_block", "minecraft:command_block_minecart",
            "minecraft:debug_stick", "minecraft:end_portal_frame", "minecraft:infested_chiseled_stone_bricks",
            "minecraft:infested_cobblestone", "minecraft:infested_cracked_stone_bricks", "minecraft:infested_deepslate",
            "minecraft:infested_mossy_stone_bricks", "minecraft:infested_stone", "minecraft:infested_stone_bricks",
            "minecraft:jigsaw", "minecraft:knowledge_book", "minecraft:light", "minecraft:mob_spawner",
            "minecraft:petrified_oak_slab", "minecraft:reinforced_deepslate", "minecraft:repeating_command_block",
            "minecraft:spawner", "minecraft:structure_block", "minecraft:structure_void", "minecraft:test_block",
            "minecraft:test_instance_block", "minecraft:trial_spawner", "minecraft:vault",
            "minecraft:bat_egg", "minecraft:blaze_egg", "minecraft:cave_spider_egg", "minecraft:cow_egg",
            "minecraft:donkey_egg", "minecraft:elder_guardian_egg", "minecraft:enderman_egg", "minecraft:endermite_egg",
            "minecraft:evoker_egg", "minecraft:ghast_egg", "minecraft:guardian_egg", "minecraft:horse_egg",
            "minecraft:husk_egg", "minecraft:llama_egg", "minecraft:magma_cube_egg", "minecraft:mooshroom_egg",
            "minecraft:mule_egg", "minecraft:ocelot_egg", "minecraft:parrot_egg", "minecraft:pig_egg",
            "minecraft:polar_bear_egg", "minecraft:rabbit_egg", "minecraft:sheep_egg", "minecraft:shulker_egg",
            "minecraft:silverfish_egg", "minecraft:skeleton_egg", "minecraft:skeleton_horse_egg", "minecraft:slime_egg",
            "minecraft:spider_egg", "minecraft:squid_egg", "minecraft:stray_egg", "minecraft:vex_egg",
            "minecraft:villager_egg", "minecraft:vindicator_egg", "minecraft:witch_egg", "minecraft:wither_skeleton_egg",
            "minecraft:wolf_egg"
    );

    private PEEmcBlacklist() {}

    public static boolean contains(String id) {
        if (id == null || EXACT.contains(id)) return true;
        return id.startsWith("minecraft:") && (id.endsWith("_spawn_egg")
                || (id.startsWith("minecraft:mob_") && id.endsWith("_face")));
    }

    public static boolean contains(ItemStack stack) {
        return !stack.isEmpty() && contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
    }
}
