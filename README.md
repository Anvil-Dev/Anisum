# Anisum

[English](README.en.md)

Anisum 是一个基于 NeoForge 的 Minecraft 模组，用于从战利品表中收集物品并同步到客户端创意标签页，便于资源整合与内容浏览。

## 特性

- 通过数据包 JSON 配置自定义标签页来源与排序。
- 支持 `include` 规则：精确匹配、通配符、正则表达式。
- 服务端重载后自动重新扫描战利品表并同步给玩家。
- 支持在玩家加入时增量同步标签页内容。

## 兼容与环境

- Minecraft: `1.21.11`
- NeoForge: `21.11.38-beta`
- Java: `21`
- Mod ID: `anisum`

> 以上版本来自当前仓库构建配置（`gradle.properties` 与 `gradle/libs.versions.toml`）。

## 快速开始（开发环境）

```powershell
# 构建
.\gradlew build

# 启动客户端调试
.\gradlew runClient

# 启动服务端调试
.\gradlew runServer

# 运行数据生成
.\gradlew runData
```

## 配置说明

Anisum 通过资源重载监听器读取 JSON 配置，配置目录前缀为 `anisum`。通常放置在：

- `data/<namespace>/anisum/<name>.json`

示例：

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

字段说明：

- `location`：标签页标识（`namespace:path`）。
- `name`：标签页显示名（文本组件）。
- `icon`：可选，标签图标（未提供时为空）。
- `include`：可选，要纳入的战利品表规则列表。
- `sort`：可选，标签中物品排序规则列表。

`include` / `sort` 规则支持：

- 完整路径：`namespace:path/to/table`
- 通配符：`namespace:path/to/*_cell`
- 正则：`namespace:path/to/.*_cell`

## 项目结构

- `src/main/java/dev/anvilcraft/resource/anisum/Anisum.java`：模组入口。
- `src/main/java/dev/anvilcraft/resource/anisum/feat/AnisumConfigManager.java`：配置加载与重载。
- `src/main/java/dev/anvilcraft/resource/anisum/feat/AnisumLootTablesLoader.java`：战利品表扫描与同步。
- `src/main/templates/META-INF/neoforge.mods.toml`：模组元信息模板。

## 许可证

- 代码：`GNU LGPL 3.0`（见 [`LICENSE`](./LICENSE)）
- 资源：默认保留所有权利，除非另有声明（见 [`ASSETS_LICENSE`](./ASSETS_LICENSE)）
