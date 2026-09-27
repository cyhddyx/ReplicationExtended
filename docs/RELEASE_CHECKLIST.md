# Release checks and roadmap

Target: Replication Extended 0.1.0-beta, Minecraft 1.21.1, NeoForge.

## Before promoting a stable release

- Test empty, partly filled, and full tanks of all eight types in game.
- Drain each tank completely and attempt to insert all other matter types.
- Save/reload the world, unload/reload chunks, and break/place filled tanks.
- Test dedicated server startup and multiplayer synchronization, with and without Jade.
- Verify the tank windows and dynamic matter level in game. Blockbench renders are not gameplay screenshots.
- Verify crafting with a filled input tank. Current standard crafting recipes do not preserve input tank contents.
- Fix the priority editor: its filter accepts arbitrary text starting with `-`, while the responder parses it as an integer. It also launches a thread per edit and updates the widget off the client thread.
- Preserve configured priority when an empty tank is broken: current drop serialization only runs for nonempty tanks.
- Add a project icon and actual gameplay screenshots to the CurseForge page.

## Suggested improvements, in priority order

1. Always show the locked matter type in item tooltips and Jade, including empty tanks.
2. Safe upgrade recipes that preserve contents, or reject nonempty input tanks.
3. Distinct per-matter symbols in addition to color, so tanks remain identifiable without color alone.
4. Comparator output proportional to fill level, followed by configurable low-stock alerts.
5. Capacity upgrades that preserve matter, type, and priority.
6. A compact network stock monitor showing all eight matter balances.

## Publication

- GitHub repository: https://github.com/cyhddyx/ReplicationExtended
- Upload only the addon release JAR; do not bundle the original Replication JAR.
- CurseForge project type: Minecraft Mods; loader: NeoForge; game version: 1.21.1; release channel: Beta.
- Required project dependencies: Replication and Titanium. Optional integration: Jade.
- Use the actual tested dependency versions when describing compatibility.
- Keep Replication's MIT notice and the MDK template license in source and binary distributions.
- Current addon license metadata remains All Rights Reserved.
