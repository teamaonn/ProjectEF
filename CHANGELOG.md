# Changelog

All notable changes to ProjectEF are documented here.

## [1.2.0] - 2026-07-29

### Added

- REI (Roughly Enough Items) recipe viewer integration via the shared `RecipeViewerHelper` abstraction, with Energy Collector fuel upgrade and World Transmutation display categories and workstation registrations.
- Trinkets accessory integration replacing the removed NeoForge Curios support.
  - 19 ProjectE items (rings, amulets, charms, Klein Stars) are registered as Trinkets and can be equipped in the `hand/ring` and `chest/necklace` slots.
  - Trinkets inventory is exposed as `IItemHandler` for ProjectE's polling-based systems (fuel consumption, Repair Talisman, hotbar-or-accessory ability checks).
  - Arcana ring attributes apply while worn via `IExposesCurioAttributes` and `Trinket.getModifiers`.
  - Data-driven slot assignment: `data/trinkets/entities/projecte.json` grants `hand/ring` and `chest/necklace` slots to players.
  - Item tags: `trinkets:hand/ring`, `trinkets:chest/necklace`, and `trinkets:all` ensure items are accepted by Trinkets slot validators.
- `rei_client` entrypoint in `fabric.mod.json` for the REI plugin.
- `trinkets` `modLocalRuntime` dependency for dev environment testing.

### Changed

- `PETags` curios tag constants updated to Trinkets `group/slot` format (`hand/ring`, `chest/necklace`, `legs/belt`).
- `PEItemTagsProvider` datagen tag assignments aligned with new Trinkets tag paths.
- `buildSrc` directory restored (was previously deleted).
- `.gitignore` updated to properly track Trinkets integration files.

### Removed

- Stale datagen-generated Curios tag files (`data/curios/tags/item/`).
- NeoForge Curios placeholder references in platform documentation.

### Known Limitations

- Trinkets accessory rendering on the player model (`TrinketRenderer`) is not implemented.
- REI item subtype comparison (distinguishing items by mode or stored EMC) is not yet ported.
- Ring passive tick effects remain gated by the `hotBarOrOffHand` inventory slot check, matching the existing item design.

## [1.1.0] - 2026-07-21

### Fabric Port

This release introduces the Fabric 1.21.1 edition of ProjectE under the name **ProjectEF**. The `F` stands for Fabric.

### Added

- Fabric Loader support for Minecraft 1.21.1.
- Fabric-safe public API primitives, registries, events, networking, capabilities, and data attachments.
- EMC mapping, recipe mapping, custom conversions, world transmutation, and server-to-client synchronization.
- ProjectE items, blocks, entities, recipes, containers, block entities, and client rendering.
- Energy Collectors, Anti-Matter Relays, Energy Condensers, Dark Matter Furnaces, Red Matter Furnaces, and related automation support.
- Transmutation Table, Transmutation Tablet, Philosopher's Stone, Alchemical Bags, Pedestals, and Eternal Density functionality.
- JEI and EMI recipe viewer integrations.
- Optional Jade and WTHIT integrations when those mods are installed.
- Built-in EMC values for classic Avaritia items.
- Fabric-compatible recipe load conditions for generated recipes and advancements.
- Expanded Simplified Chinese translations and updated localization metadata.

### Restored and Fixed

- Creative flight and ring-provided flight behavior.
- GEM armor abilities, including helmet night vision and boot step assistance.
- Dark Matter and Red Matter tool area modes, including 3x3-style modes.
- Philosopher's Stone world transmutation, conversion cycling, selection range rendering, and recipe interactions.
- Dark Matter and Red Matter Furnace fuel handling for ProjectE fuels.
- Volcanite Amulet lava placement and projectile behavior.
- Pedestal item rendering for rings and other supported items.
- `/project` client diagnostics command registration and execution.
- EMC reload behavior after editing EMC configuration or mapper data.
- Red Matter Sword and Red Matter Katar instant-kill modes for high-health entities.
- Fabric permissions compatibility for Minecraft 1.21.1 and Xaero map integrations.

### Platform and Compatibility Changes

- Replaced NeoForge build tooling with Fabric Loom 1.10.5, Mojang mappings, and Parchment mappings.
- Added Fabric access wideners, mixin configuration, and Fabric client/server entrypoints.
- Added Forge Config API Port as a required runtime dependency.
- Bundled the Minecraft 1.21.1-compatible `fabric-permissions-api` 0.3.1 release.
- Preserved the internal mod ID `projecte` and the existing `config/ProjectE` configuration directory.
- Removed or deferred NeoForge-only integrations that do not have a compatible Fabric 1.21.1 implementation, including CraftTweaker and The One Probe.
- Artifact names use the format `ProjectEF-1.21.1-PE1.1.0.jar`.

### Requirements

- Minecraft 1.21.1.
- Fabric Loader 0.16.9 or newer.
- Fabric API for Minecraft 1.21.1.
- Forge Config API Port 21.1.0 or newer.
- Java 21.

### Known Limitations

- This release targets Fabric only; NeoForge-specific integrations are not available.
- REI and Trinkets integrations are now available as of version 1.2.0.
- The legacy NeoForge test framework and data generator are not part of the current Fabric build.

