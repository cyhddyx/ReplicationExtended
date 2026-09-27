# Replication Extended

An unofficial addon for [Replication](https://www.curseforge.com/minecraft/mc-mods/replication), maintained by cyhddyx.

Adds eight dedicated matter tanks. Each tank is permanently bound to one matter type, including when empty, so automated matter networks cannot fill an emptied tank with a different type.

## Features

- Dedicated tanks for Earth, Nether, Organic, Ender, Metallic, Precious, Living, and Quantum matter.
- Distinct matter-colored tank textures and dynamic stored-matter rendering.
- Connections to Replication's matter network through the top and bottom.
- Configurable network priority through the tank interface.
- Stored matter is carried by the dropped tank and restored when placed.
- Optional Jade integration and stored-matter item tooltips.
- English and Simplified Chinese translations.

## Installation

Use Minecraft **1.21.1**, **NeoForge 21.1.250 or later in the 21.1 series**, and Java 21.
Install this addon on both the client and server, together with Replication and Titanium and any dependencies required by those mods. Jade is optional.

The current build uses Replication **1.21.1-1.2.7** and Titanium **1.21-4.0.42**. The loader metadata permits older versions, but those combinations have not been verified here. This is a beta release; back up worlds before testing.

Download the addon JAR from [Releases](https://github.com/cyhddyx/ReplicationExtended/releases) when available. The original Replication mod is a separate dependency and is not bundled.

## Crafting

Surround a Replication matter tank with four matching catalysts in a cross. **Empty the source tank before crafting: these standard recipes do not transfer its stored matter.**

| Matter | Catalyst |
| --- | --- |
| Earth | Dirt |
| Nether | Netherrack |
| Organic | Bone meal |
| Ender | Ender pearl |
| Metallic | Iron ingot |
| Precious | Gold ingot |
| Living | Slime ball |
| Quantum | Amethyst shard |

## Build

Install JDK 21 and place the Replication source checkout next to this addon as `../Replication`. Build Replication first so its compiled `Replication-*.jar` is available in `../Replication/build/libs/`. Then run `./gradlew build` (Windows: `.\gradlew.bat build`) in this addon. The addon reads that local Replication JAR; it does not download Replication through Gradle. The addon JAR is generated in `build/libs/`.

The GitHub build workflow also needs that local dependency prepared; checking out this addon alone is not sufficient.

## Status and Feedback

Version **0.1.0-beta**. A successful build does not replace in-game testing. See [release checks and planned improvements](docs/RELEASE_CHECKLIST.md) for outstanding validation and known limitations.

Report problems in [Issues](https://github.com/cyhddyx/ReplicationExtended/issues), including mod versions, reproduction steps, and relevant logs with private information removed.

## Credits and Licensing

Replication is created by Buuz135 and contributors. Tank geometry, textures, and portions of the integration/rendering code are derived from Replication and retain its MIT notice in [THIRD_PARTY_NOTICES.txt](THIRD_PARTY_NOTICES.txt).

Original addon contributions retain the project's existing **All Rights Reserved** policy; publishing the source does not relicense it as MIT. The NeoForged MDK template is covered separately by [TEMPLATE_LICENSE.txt](TEMPLATE_LICENSE.txt).
