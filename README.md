# ProjectEF

ProjectEF is a focused Fabric port of the parts of ProjectE that fit this project: a practical EMC shop built around the Transmutation Table and Transmutation Tablet.

It is designed for **Minecraft 26.3** with **Fabric**. The goal is to make EMC useful for farmed resources and normal survival progression without bringing back the parts of ProjectE that add unnecessary grinding or complexity.

## Current version

**1.1.30-fabric.26.3**

## Included

- Transmutation Table
- Transmutation Tablet
- Server-side EMC accounts and learned-item data
- Vanilla EMC values tuned for this project
- EMC support for registered mod items
- World-specific EMC overrides
- Energy Condenser
- Dark Matter Furnace
- Red Matter Furnace
- Custom ProjectE-style items, including the Black Hole Band

## EMC shop

The table and tablet use the same shop system:

1. Sell a valued item to receive EMC.
2. Selling an item teaches it to that player.
3. Spend stored EMC to buy learned items.
4. Purchases create a normal, unmodified item stack.

The system is server-authoritative, so prices, balances, learning, selling, and buying are checked on the server.

Modded items are supported as long as they have a registered item ID and an EMC value. Items from namespaces other than `minecraft:` are not automatically blocked.

## Black Hole Band

The Black Hole Band uses this custom recipe:

- 4 Ender Pearls
- 4 Gold Ingots
- 1 Iron Block

Its EMC value is **14,536**, matching the total EMC of those ingredients.

## Survival-focused blacklist

Items that are not normally obtainable through survival gameplay are blocked from the EMC table, even if they are obtained through commands or another non-normal method.

This includes technical and creative-only vanilla entries such as:

- Bedrock
- End Portal Frames
- Command blocks
- Structure and test blocks
- Spawners and other technical spawner entries
- Infested blocks
- Budding Amethyst
- Reinforced Deepslate
- Creative-only spawn eggs
- Technical mob-face and legacy egg entries

Normal survival items remain available, including items such as the Dragon Egg, Turtle Egg, Sniffer Egg, Player Head, Wither Skeleton Skull, and Dragon Head.

## Commands

Operators can set an EMC value for the item being held:

```
/projecte setemc <emc>
```

Or set a value by item ID:

```
/projecte setemc <emc> <namespace:item_id>
```

Example:

```
/projecte setemc 24584 minecraft:diamond_pickaxe
```

Set an item to `0` to disable its EMC value. Changes are world-specific and persist in the world data.

## Building

Use the included Gradle wrapper with Java 21:

```
./gradlew build
```

On Windows PowerShell:

```
.\\gradlew.bat build
```

The project targets Fabric Loader `0.19.5`, Fabric API for Minecraft 26.3, and Fabric Loom `1.18.2`.

## Scope

ProjectEF is intentionally not a full restoration of every original ProjectE system. The table, tablet, EMC economy, selected utility items, condensers, and matter furnaces are the core of this version. Features that do not serve the simplified survival-focused design may remain absent.

For the detailed EMC registry and balancing notes, see [EMC-SHOP-README.md](EMC-SHOP-README.md).
