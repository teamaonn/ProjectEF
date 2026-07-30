<p align="center">
  <img src="src/main/resources/assets/projecte/logo.png" alt="ProjectEF Logo" width="220">
</p>
<h1 align="center">ProjectEF</h1>
<p align="center">
  <b>Equivalent Exchange: Reborn for Fabric</b><br>
  中文名：[Fabric]等价交换:重置版<br>
  Minecraft 1.21.1 Fabric Edition
</p>
<p align="center">
<!-- <a href="https://modrinth.com/mod/projectef">
<img src="https://img.shields.io/modrinth/dt/projectef?logo=modrinth&label=Modrinth%20Downloads&color=00AF5C" alt="Modrinth Downloads">
</a> -->
<a href="https://www.curseforge.com/minecraft/mc-mods/projectef">
<img src="https://img.shields.io/curseforge/dt/1618732?logo=curseforge&label=CurseForge%20Downloads&color=F16436" alt="CurseForge Downloads">
</a>
<a href="https://github.com/wchiway/ProjectEF/releases">
<img src="https://img.shields.io/github/v/release/wchiway/ProjectEF?logo=github&label=Latest%20Release" alt="Latest Release">
</a>
<a href="https://github.com/wchiway/ProjectEF/blob/mc1.21.1/LICENSE">
<img src="https://img.shields.io/github/license/wchiway/ProjectEF?label=License" alt="License">
</a>

</p>

## Overview

**ProjectEF** is a modern Fabric port inspired by the classic **Equivalent Exchange 2** and **ProjectE** systems.

The project focuses on bringing the complete EMC-based transmutation experience to Minecraft Fabric, including item value systems, transmutation devices, collectors, condensers, powerful matter tools, and alchemical equipment.

ProjectEF is designed for **Minecraft 1.21.1 Fabric** while maintaining compatibility with the original ProjectE data structure where possible.

## Project Status

| Component    | Version               |
| ------------ | --------------------- |
| Minecraft    | 1.21.1                |
| ProjectEF    | 1.2.1                 |
| Mod Loader   | Fabric Loader 0.16.9+ |
| Fabric API   | 0.116.14+1.21.1       |
| Java         | 21                    |
| Build System | Fabric Loom 1.10.5    |

## Features

### EMC Transmutation System

The core of ProjectEF is the EMC (Energy-Matter Covalence) system.

Features include:

* Item EMC values
* Learning and storing item knowledge
* Item conversion
* Energy collection
* Matter condensation

### Alchemical Machines

ProjectEF includes classic transmutation equipment:

* Transmutation Table
* Transmutation Tablet
* Energy Collectors
* Energy Condensers
* Alchemical Bags
* Eternal Density

### Matter Equipment

Powerful tools and equipment based on EMC technology:

* Dark Matter Tools
* Red Matter Tools
* Dark Matter Furnace
* Red Matter Furnace
* GEM Armor
* Swiftwolf's Rending Gale

Supported abilities include:

* Flight support
* Night vision
* Area mining modes
* Movement enhancement

### Philosopher's Stone

The Philosopher's Stone allows players to manipulate the world through alchemical conversion.

Features include:

* Block transformation
* Selection rendering
* World transmutation recipes

## Fabric Port Progress

The Fabric migration has completed the main systems:

* Public API
* Registration system
* Networking
* Event handling
* Configuration system
* EMC data management
* Items and blocks
* Entities
* Block entities
* Recipes
* Containers
* Client rendering
* Runtime hooks

Additional restored features:

* Creative and ring-based flight
* GEM Armor abilities
* Dark Matter and Red Matter tool modes
* ProjectEF furnace fuel support
* Fabric-compatible recipe conditions
* Client rendering integrations
* Built-in EMC values for classic Avaritia items
* Expanded Simplified Chinese translations

## Installation

### Requirements

* Minecraft 1.21.1
* Fabric Loader 0.16.9 or newer
* Fabric API
* Java 21

### Steps

1. Install Minecraft 1.21.1 with Fabric Loader.
2. Install a compatible Fabric API version.
3. Install Forge Config API Port.
4. Download the latest ProjectEF release.
5. Place the JAR file into your Minecraft `mods` folder.

The permissions API is included inside the ProjectEF JAR.

Optional integrations:

* JEI
* EMI
* REI
* Jade
* WTHIT

## Building from Source

Clone the repository:

```bash
git clone https://github.com/wchiway/ProjectEF.git

cd ProjectEF
```

Build the project:

```bash
./gradlew build
```

Windows:

```bat
gradlew.bat build
```

Build output:

```
build/libs/
```

Run the Fabric development client:

```bash
./gradlew runClient
```

The development recipe viewer defaults to JEI. The `recipe_viewer` property accepts
`jei`, `emi`, `rei`, `hybrid`, or `none`.

To use EMI or REI:

```bash
./gradlew runClient -Precipe_viewer=emi
./gradlew runClient -Precipe_viewer=rei
```

## Compatibility Notes

ProjectEF keeps the original ProjectE mod identifier:

```
projecte
```

This allows compatibility with:

* Existing configuration files
* Existing resource paths
* Existing world data

Accessory slots are provided through [Trinkets](https://modrinth.com/mod/trinkets).
Install it to equip rings, amulets, charms, and Klein Stars in the `hand/ring` and
`chest/necklace` slots. ProjectEF works without it; the accessories simply stay
regular inventory items.

The following integrations from the NeoForge version are not included:

* CraftTweaker
* The One Probe
* Curios (replaced by Trinkets on Fabric)

## Reporting Issues

When reporting a bug, please include:

* ProjectEF version
* Minecraft version
* Fabric Loader version
* Fabric API version
* Steps to reproduce
* Relevant game logs

Please attach logs as files or external paste links instead of posting complete logs directly in the issue.

## Developer

**Chiway Wang**

Fabric port developer and maintainer.

## Credits

ProjectEF is based on the original **ProjectE** project.

Special thanks to:

* ProjectE developers and contributors
* EE2 original creators
* Minecraft modding community

Original contributors include:

* SinKillerJ
* pupnewfster
* MaPePeR
* williewillus
* Lilylicious
* MozeIntel
* Kolatra

Additional credits:

* x3n0ph0b3 — EE2 creator and original asset permissions
* MidnightLightning — EE2 GUI textures

## License

ProjectEF is released under the MIT License.
