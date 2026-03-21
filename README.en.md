<div align="center">

# Anisum

<img src=".idea/icon.png" style="width: 128px; height: 128px" alt="Anisum Logo">

</div>

[中文说明](README.md)

Anisum is a NeoForge-based Minecraft mod that collects items from loot tables and syncs them into client creative tabs for easier content organization and browsing.

## Features

- Define tab sources and ordering through datapack JSON files.
- `include` rules support exact match, wildcard, and regex patterns.
- Automatically reprocesses loot tables after server datapack reload.
- Syncs tab payloads to players on join.

## Compatibility

- Minecraft: `1.21.11`
- NeoForge: `21.11.38-beta`
- Java: `21`
- Mod ID: `anisum`

> Versions above are taken from the current repository build configuration (`gradle.properties` and `gradle/libs.versions.toml`).

## Quick Start (Dev)

```powershell
# Build
.\gradlew build

# Run client
.\gradlew runClient

# Run dedicated server
.\gradlew runServer

# Run data generation
.\gradlew runData
```

## Configuration

Anisum reads JSON definitions via a resource reload listener with the `anisum` directory prefix. Typical location:

- `data/<namespace>/anisum/<name>.json`

Example:

```json
{
  "location": "anisum:demo",
  "name": { "translate": "itemGroup.anisum.demo" },
  "include": [
    "examplemod:chests/simple_dungeon",
    "examplemod:chests/*",
    "examplemod:chests/.*_rare"
  ],
  "sort": [
    "examplemod:chests/simple_dungeon",
    "examplemod:chests/*"
  ]
}
```

Field notes:

- `location`: target tab identifier (`namespace:path`).
- `name`: tab display name (text component).
- `icon`: optional tab icon (empty if omitted).
- `include`: optional list of loot table matching rules.
- `sort`: optional list of ordering rules.

Supported rule formats for `include` / `sort`:

- Full path: `namespace:path/to/table`
- Wildcard: `namespace:path/to/*_cell`
- Regex: `namespace:path/to/.*_cell`

## Project Layout

- `src/main/java/dev/anvilcraft/resource/anisum/Anisum.java`: mod entrypoint.
- `src/main/java/dev/anvilcraft/resource/anisum/feat/AnisumConfigManager.java`: config load/reload.
- `src/main/java/dev/anvilcraft/resource/anisum/feat/AnisumLootTablesLoader.java`: loot scan and sync.
- `src/main/templates/META-INF/neoforge.mods.toml`: mod metadata template.

## License

- Code: `GNU LGPL 3.0` (see `LICENSE`)
- Assets: all rights reserved unless explicitly stated (see `ASSETS_LICENSE`)
