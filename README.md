<p align="center">
  <img src="src/main/resources/logo.png" alt="ProjectEF logo" width="256">
</p>

# ProjectEF for Fabric 1.21.1

ProjectEF is a modern reimplementation of EE2 (Equivalent Exchange 2), centered on EMC-based transmutation, collectors, condensers, matter tools, flying rings, and other alchemical equipment.

This repository contains the Fabric 1.21.1 port maintained by [Chiway Wang](https://github.com/wchiway). The `F` in ProjectEF means Fabric. Source code, releases, and issue tracking are hosted at [github.com/wchiway/ProjectEF](https://github.com/wchiway/ProjectEF).

## Port status

| Component | Status |
| --- | --- |
| Minecraft | 1.21.1 |
| ProjectEF version | 1.1.0 |
| Mod loader | Fabric Loader 0.16.9 or newer |
| Fabric API | 0.116.14+1.21.1 |
| Java | 21 |
| Build system | Fabric Loom 1.10.5 with Mojang and Parchment mappings |

The public API, registration system, networking, events, configuration, EMC mapping, items, blocks, entities, recipes, block entities, containers, client rendering, and runtime hooks have been migrated to Fabric.

Recent port work also restores:

- Creative and ring-provided flight behavior.
- GEM armor abilities, including helmet night vision and boot step assist.
- Dark Matter and Red Matter tool area modes.
- Philosopher's Stone world transmutation, selection rendering, and recipe interactions.
- ProjectEF fuel support in Dark Matter and Red Matter Furnaces.
- Pedestal, alchemical bag, Eternal Density, and other container interactions.
- Fabric-compatible recipe conditions and client rendering hooks.
- Built-in EMC values for classic Avaritia items.
- Expanded Simplified Chinese translations.

## Installation

1. Install Minecraft 1.21.1 with Fabric Loader 0.16.9 or newer.
2. Install a compatible Fabric API release.
3. Download the ProjectEF JAR from this repository's [Releases](https://github.com/wchiway/ProjectEF/releases), or build it from source.
4. Place the JAR in the Minecraft `mods` directory.

Forge Config API Port and the permissions API are bundled in the ProjectEF JAR. Optional recipe display support is available for JEI and EMI; Jade and WTHIT integrations are also included when those mods are installed.

## Building from source

Clone the repository and run the Gradle build:

```bash
git clone https://github.com/wchiway/ProjectEF.git
cd ProjectEF
./gradlew build
```

On Windows, use:

```bat
gradlew.bat build
```

Build artifacts are written to `build/libs/`. To launch a Fabric development client, run `./gradlew runClient` or `gradlew.bat runClient`.

The development recipe viewer defaults to JEI. It can be changed with the `recipe_viewer` Gradle property, for example:

```bash
./gradlew runClient -Precipe_viewer=emi
```

## Compatibility notes

- CraftTweaker, The One Probe, and Curios integrations from the NeoForge codebase are not part of this Fabric port.
- Trinkets support is planned but is not currently implemented.
- Generated recipes are shipped with Fabric load conditions; the legacy NeoForge test framework and data generator are not run by the current Fabric build.
- The internal Fabric mod ID remains `projecte`, and existing configuration files remain under `config/ProjectE`, so worlds, resources, and settings stay compatible.

## Reporting issues

Report bugs through the [GitHub issue tracker](https://github.com/wchiway/ProjectEF/issues). Include the ProjectEF version, Fabric Loader and Fabric API versions, a minimal reproduction procedure, and the relevant game log. Attach logs as files or use a paste service instead of placing an entire log directly in the issue body.

## Developer

- [Chiway Wang](https://github.com/wchiway) — Fabric port developer and maintainer

## Upstream and credits

This port is based on the original [ProjectE](https://github.com/sinkillerj/ProjectE) project. Credit for the original implementation and prior version work belongs to its maintainers and contributors, including SinKillerJ, pupnewfster, MaPePeR, williewillus, Lilylicious, MozeIntel, and Kolatra.

- x3n0ph0b3 — EE2 creator and original asset permission
- MidnightLightning — EE2 GUI textures

## License

ProjectEF is distributed under the [MIT License](LICENSE).
