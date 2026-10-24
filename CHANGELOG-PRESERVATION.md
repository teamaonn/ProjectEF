# 1.1.33-fabric.26.3-preservation.2

Durability wear is no longer stored as part of learned item variants. Selling the
same item at different damage levels learns one entry; purchases return its
undamaged version. Enchantments, names, potion contents, model identity, and
other saved components retain their existing preservation behavior.

Existing accounts are upgraded on access: damaged ordinary items become ordinary
learned items, and variants differing only by wear are merged. EMC balances and
unrelated learned entries remain intact. Migration does not apply the optional
preservation switches to saved records, so temporarily disabling those switches
does not erase enchantments or cosmetic data. The upgraded account is persisted.

The pricing logic, furnaces, and other features from preservation.1 are unchanged.
Server and client compilation passed; all six required headless Minecraft tests passed.

Regression coverage is in `src/gametest`; run `gradle runGameTest` with Java 25.
It checks damage-independent identity, existing-account migration and balances,
disabled-config preservation, serialization, and an actual damaged enchanted
cosmetic sword sale/purchase round trip in a Minecraft server.

# 1.1.33-fabric.26.3-preservation.1

Adds configurable preservation of item data to the EMC shop. Named items,
enchanted equipment, potions, and cosmetic variants can be learned and purchased
as distinct entries with their saved components. Learning a variant does not
unlock its ordinary counterpart. Existing balances and learned base items are
retained, and identical variants are deduplicated.

The new `config/projecte-preservation.properties` enables preservation by default.
Individual switches control custom names, enchantments, potion contents and
custom data. Existing properties files are respected. Disabling the master switch
keeps saved variants stored while restoring base-item learning behavior.

Retains both matter furnaces and all other features of the current 26.3 branch.
The Dark Matter and Red Matter furnace block/item identifiers and shared block
entity are unchanged. Existing EMC values, overrides, blacklist and shop inventory
layout are retained. Blacklist cleanup now preserves learned variant records.
Filled portable inventories are not learned with their stored contents.

## Validation

- Server and client compilation passed using Java 25.
- 30 first-run server checks passed, including furnace registration, placement
  and smelting; exact enchanted-item and Glass Sword purchases; serialization;
  legacy accounts; blacklist behavior; and filled-container handling.
- 11 full-restart checks passed, including furnace inventories and saved variants.
- In-game testing confirmed the Glass Sword and its ProjectEF learning/purchase
  round trip.

Install the JAR linked in the main README. Replace older ProjectEF JARs and use
this version on both clients and servers. Fabric API is required; ItemBound is
optional and distributed separately. Keep a world backup when upgrading.
