package moze_intel.projecte;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class PERegistries {

    private static final Map<String, Item> ITEMS = new LinkedHashMap<>();
    private static final Map<String, Block> BLOCKS = new LinkedHashMap<>();
    private static Block transmutationTable;
    private static Item transmutationTablet;

    private PERegistries() {
    }

    public static void register() {
        registerItems();
        registerBlocks();
    }

    private static void registerItems() {
        simpleItem("philosophers_stone", noStack());
        simpleItem("repair_talisman", noStack());
        simpleItem("low_covalence_dust");
        simpleItem("medium_covalence_dust");
        simpleItem("high_covalence_dust");

        for (String color : new String[] {"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"}) {
            simpleItem(color + "_alchemical_bag", noStack());
        }

        for (String tier : new String[] {"ein", "zwei", "drei", "vier", "sphere", "omega"}) {
            simpleItem("klein_star_" + tier, noStack());
        }

        for (String name : new String[] {"alchemical_coal", "mobius_fuel", "aeternalis_fuel", "dark_matter", "red_matter", "iron_band"}) {
            simpleItem(name);
        }

        for (String name : new String[] {
                "dm_pick", "dm_axe", "dm_shovel", "dm_sword", "dm_hoe", "dm_shears", "dm_hammer",
                "rm_pick", "rm_axe", "rm_shovel", "rm_sword", "rm_hoe", "rm_shears", "rm_hammer", "rm_katar", "rm_morning_star",
                "dm_helmet", "dm_chestplate", "dm_leggings", "dm_boots",
                "rm_helmet", "rm_chestplate", "rm_leggings", "rm_boots",
                "gem_helmet", "gem_chestplate", "gem_leggings", "gem_boots",
                "black_hole_band", "archangel_smite", "harvest_goddess_band", "ignition_ring", "zero_ring", "swiftwolf_rending_gale",
                "watch_of_flowing_time", "evertide_amulet", "volcanite_amulet", "gem_of_eternal_density", "mercurial_eye", "void_ring",
                "arcana_ring", "body_stone", "soul_stone", "mind_stone", "life_stone",
                "divining_rod_1", "divining_rod_2", "divining_rod_3", "destruction_catalyst", "hyperkinetic_lens", "catalytic_lens",
                "tome"
        }) {
            simpleItem(name, noStackFireResistant());
        }
        transmutationTablet = simpleItem("transmutation_tablet", noStackFireResistant());
    }

    private static void registerBlocks() {
        block("alchemical_coal_block", fuelBlock(MapColor.COLOR_RED));
        block("mobius_fuel_block", fuelBlock(MapColor.COLOR_RED));
        block("aeternalis_fuel_block", fuelBlock(MapColor.COLOR_LIGHT_GRAY));
        block("dark_matter_block", matterBlock(MapColor.COLOR_BLACK), fireResistant());
        block("red_matter_block", matterBlock(MapColor.COLOR_RED), fireResistant());
        transmutationTable = transmutationTable("transmutation_table", stone(10.0F, 30.0F));
    }

    public static boolean isTransmutationTable(Block block) {
        return block == transmutationTable;
    }

    public static boolean isTransmutationTablet(Item item) {
        return item == transmutationTablet;
    }

    private static Item simpleItem(String name) {
        return simpleItem(name, Function.identity());
    }

    private static Item simpleItem(String name, Function<Item.Properties, Item.Properties> properties) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, PECore.id(name));
        Item item = new Item(properties.apply(new Item.Properties().setId(key)));
        Registry.register(BuiltInRegistries.ITEM, key, item);
        ITEMS.put(name, item);
        return item;
    }

    private static Block block(String name, BlockBehaviour.Properties properties) {
        return block(name, properties, Function.identity());
    }

    private static Block block(String name, BlockBehaviour.Properties properties, Function<Item.Properties, Item.Properties> itemProperties) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, PECore.id(name));
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, PECore.id(name));
        Block block = new Block(properties.setId(blockKey));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
        Registry.register(BuiltInRegistries.ITEM, itemKey, new BlockItem(block, itemProperties.apply(new Item.Properties().setId(itemKey))));
        BLOCKS.put(name, block);
        return block;
    }

    private static Block transmutationTable(String name, BlockBehaviour.Properties properties) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, PECore.id(name));
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, PECore.id(name));
        Block block = new TransmutationTableBlock(properties.setId(blockKey));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
        Registry.register(BuiltInRegistries.ITEM, itemKey, new BlockItem(block, new Item.Properties().setId(itemKey)));
        BLOCKS.put(name, block);
        return block;
    }

    private static BlockBehaviour.Properties fuelBlock(MapColor color) {
        return BlockBehaviour.Properties.of().mapColor(color).strength(0.5F, 1.5F);
    }

    private static BlockBehaviour.Properties stone(float hardness, float resistance) {
        return BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(hardness, resistance);
    }

    private static BlockBehaviour.Properties matterBlock(MapColor color) {
        return BlockBehaviour.Properties.of().mapColor(color).requiresCorrectToolForDrops().strength(1_000_000.0F, 3_000_000.0F).lightLevel(state -> 14);
    }

    private static Function<Item.Properties, Item.Properties> noStack() {
        return properties -> properties.stacksTo(1);
    }

    private static Function<Item.Properties, Item.Properties> fireResistant() {
        return Item.Properties::fireResistant;
    }

    private static Function<Item.Properties, Item.Properties> noStackFireResistant() {
        return properties -> properties.stacksTo(1).fireResistant();
    }
}
