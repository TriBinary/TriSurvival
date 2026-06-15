# TriSurvival — Agent Instructions

## Project Overview

TriSurvival is a Minecraft Paper plugin that provides an RPG progression system: skills, stats, gear bonuses, custom
fishing (sea creatures), and a custom crafting GUI. It targets Paper 1.21 (API 26.1.2) and is written in Kotlin.

## Tech Stack

| Tool           | Version                    |
|:---------------|:---------------------------|
| Language       | Kotlin 2.3.10              |
| Build          | Gradle Kotlin DSL          |
| Platform       | Paper API 26.1.2 (MC 1.21) |
| Java toolchain | JDK 25                     |

## Build & Run

```powershell
# Build and copy JAR to run/plugins/
./gradlew build

# Run the local Paper test server (starts in run/)
./gradlew runServer
```

The Gradle build automatically copies the output JAR to `run/plugins/` after every successful build.

## Repository Layout

```
src/main/kotlin/net/trilleo/mc/plugins/trisurvival/
├── Main.kt                  # Plugin entry point
├── commands/                # Sub-commands (auto-registered)
│   ├── debug/
│   ├── info/
│   ├── moderation/
│   └── rpg/
├── config/                  # PluginConfig implementations
├── crafting/                # Custom recipe registry & ingredients
├── data/                    # JSON persistence (player + server)
├── enums/                   # Shared enumerations (FillMode, PagedGUIMode, …)
├── events/                  # Custom Bukkit events
├── fishing/                 # Sea creature framework
├── guis/                    # Inventory GUIs (auto-registered)
├── items/                   # Custom items (auto-registered)
├── listeners/               # Event listeners (auto-registered)
│   ├── crafting/
│   ├── skills/              # One XP-gain listener per skill
│   └── stats/               # One application listener per stat
├── recipes/                 # PluginRecipe implementations
├── registration/            # Auto-registration engine (do not modify lightly)
├── skills/                  # Skill enum, SkillManager, SkillConfig, SkillReward
├── stats/                   # Stat enum, StatManager, StatProfile, GearBonusReader
├── tasks/                   # Scheduled tasks (auto-registered)
└── utils/                   # Utility helpers (itemStack DSL, MessageUtil, PDCUtil, …)
```

## Auto-Registration System

The plugin uses `PackageScanner` to discover components at startup — you **never** edit `plugin.yml` or wire things
manually. Just extend the right base class and place the file in the correct package.

| Component | Base Class                     | Package                 |
|:----------|:-------------------------------|:------------------------|
| Command   | `PluginCommand`                | `commands` (any depth)  |
| Listener  | `Listener`                     | `listeners` (any depth) |
| GUI       | `PluginGUI` / `PagedPluginGUI` | `guis` (any depth)      |
| Task      | `PluginTask`                   | `tasks` (any depth)     |
| Item      | `PluginItem`                   | `items` (any depth)     |
| Recipe    | `PluginRecipe`                 | `recipes` (any depth)   |
| Config    | `PluginConfig`                 | `config` (any depth)    |
| Mob       | `CustomMob`                    | `mobs` (any depth)      |

Every class must have either a no-arg constructor or a `JavaPlugin` constructor (the plugin instance is auto-injected).

## Key Systems

### Stats (`stats/`)

31 stats defined in `Stat.kt` (enum). Each stat has a `displayName`, `symbol`, `color`, `baseValue`, and `StatCategory`.
Categories: `COMBAT`, `HEALTH`, `UTILITY`, `MINING`, `FARMING`, `FORAGING`, `FISHING`.

`StatManager` (singleton `object`) owns all live `StatProfile` instances (one per online player). Call
`StatManager.recalculate(player)` to rebuild a profile from: base values + skill bonuses + gear bonuses.

`StatProfile` is a map-backed value store. Access stats via `profile[Stat.HEALTH]` or
`StatManager.getStat(player, stat)`.

`StatRecalcEvent` fires after every recalculation — listen to it if a system needs to react to stat changes.

### Skills (`skills/`)

7 skills in `Skill.kt` (enum): `COMBAT`, `MINING`, `FARMING`, `FORAGING`, `FISHING`, `ENCHANTING`, `ALCHEMY`. Each has a
`baseXP`, XP `multiplier`, and `maxLevel` (60).

XP formula: `baseXP * multiplier^(level - 1)` per level. Use `Skill.xpForLevel(level)` and
`Skill.totalXpForLevel(level)`.

`SkillManager` holds level/XP state for every online player. `SkillConfig` maps skill levels → stat bonuses.

`SkillXPGainEvent` and `SkillLevelUpEvent` fire from `SkillManager` — listen to them for rewards or UI feedback.

### Gear Bonuses (`stats/GearBonusReader.kt`)

Items store stat bonuses as a JSON string under the PDC key `trisurvival:stat_bonuses`. The JSON maps `Stat.name` →
`Double`. Use `GearBonusReader.encodeBonuses(map)` to write the value and `GearBonusReader.parseBonuses(item)` to read
it.

`GearBonusReader.readEquippedBonuses(player)` aggregates bonuses from 6 slots: helmet, chestplate, leggings, boots, main
hand, off-hand.

### Custom Mobs (`mobs/`)

Hypixel-style custom mob system layered over vanilla entities. A mob *definition* is a Kotlin `object` extending
`CustomMob` (in the `mobs` package, any depth) and is auto-registered by `MobRegistrar` — exactly like `PluginItem`.
A definition declares `displayName`, `entityType`, `rarity` (`MobRarity`), `stats` (the shared `Stat` enum:
`HEALTH`, optional `DAMAGE`/`DEFENSE`/…), `abilities` (`MobAbility`), `drops` (`MobDrop`/`ChanceDrop`), and `baseXp`.
Passive mobs simply omit `DAMAGE`.

`MobManager` (singleton) owns live `MobInstance`s keyed by entity UUID: it spawns mobs (`spawn(id, location)`), tracks
custom health, renders a health hologram, routes all combat through `DamageFormula`, and despawns everything on
`cleanup()` (called in `onDisable`). Per-chunk density is capped by `MobManager.MAX_CUSTOM_MOBS_PER_CHUNK`.

Combat is handled by `listeners/mobs/MobDamageListener` (player↔mob, both directions) and `MobDeathListener` (drops +
Combat XP); `DamageListener` early-returns for custom-mob victims. Holograms (`hologram/Hologram`, `DamageIndicator`)
use native `TextDisplay` entities and refresh via `MobHologramTask`. `CustomMobSpawnEvent` (cancellable) and
`CustomMobDeathEvent` fire from the pipeline.

Spawn logic: `MobSpawnReplaceListener` converts natural vanilla spawns via code-registered `MobSpawnRule`s, and
`MobZoneSpawnTask` tops up persisted `MobZone`s (managed with `/mob zone …`). The vanilla nameplate is suppressed and
`MobInteractListener` blocks name-tag renaming. Use `CustomMob.isCustom(entity)` / `CustomMob.idOf(entity)` to detect.

#### Sea Creatures (`fishing/`)

`SeaCreature` is a `CustomMob` summoned by fishing instead of world spawning (concrete creatures live under
`mobs/fishing/`). `SeaCreatureRegistry` is a thin rarity-weighted view over `MobRegistrar`. `FishingStatListener` rolls
one on a Sea Creature Chance proc and calls `spawnFromHook(location, player)`, which spawns the mob and runs the
`onFishedUp` hook.

### Custom Crafting (`crafting/`)

`CraftingRecipeRegistry` manages recipes that use `CraftingIngredient` (variable amounts). The `CraftingTableGUI`
provides the player-facing crafting interface. Register recipes by adding them to the registry in `Main.onEnable` after
`CraftingRecipeRegistry` is initialised.

### Custom Events (`events/`)

| Event               | Fired by       | Payload                   |
|:--------------------|:---------------|:--------------------------|
| `SkillXPGainEvent`  | `SkillManager` | player, skill, XP amount  |
| `SkillLevelUpEvent` | `SkillManager` | player, skill, new level  |
| `StatRecalcEvent`   | `StatManager`  | player, new `StatProfile` |

All three are cancellable.

### Data Persistence (`data/`)

- **Player data** — `PlayerDataManager.get(player)` returns a `PlayerData` (JSON, auto-loaded/saved). Extend
  `PlayerData` for typed properties.
- **Server data** — `ServerDataManager.get()` returns a `ServerData` (single JSON file). Extend `ServerData` for typed
  properties.

### Utilities (`utils/`)

| Helper          | Purpose                                                                |
|:----------------|:-----------------------------------------------------------------------|
| `itemStack { }` | Kotlin DSL builder for `ItemStack` (name, lore, enchants, PDC, flags)  |
| `MessageUtil`   | Sends prefix-decorated MiniMessage messages to players                 |
| `PDCUtil`       | Typed get/set helpers for `Entity`, `Chunk`, and `ItemStack` PDC       |
| `LoreUtil`      | Word-wrapping for item lore with MiniMessage style carry-over          |
| `CountdownUtil` | Per-player countdown with action bar / title / boss bar / chat display |
| `TeamUtil`      | Custom team management backed by `ServerData`                          |
| `TagUtil`       | Per-player string tags backed by `PlayerData`                          |
| `GameRuleUtil`  | Typed read / write / toggle for Minecraft game rules                   |

## Commit Convention

Format: `<Tag>: <imperative message>` — no trailing period.

| Tag           | Use for                                   |
|:--------------|:------------------------------------------|
| `Feature`     | New functionality                         |
| `Fix`         | Bug / crash / logic error repairs         |
| `Improvement` | Refines existing code, UX, or performance |
| `Internal`    | Docs, comments, repo maintenance          |
| `Backend`     | Database schema or config changes         |
| `Update`      | Version bumps                             |

Example: `Feature: Add sea creature spawn listener`

## Code Conventions

- **Kotlin idioms** — use `object` for singletons, `data class` for value types, extension functions for utility.
- **No comments by default** — only add one when the WHY is non-obvious (hidden constraint, workaround, subtle
  invariant). Never describe WHAT the code does.
- **No unused code** — delete dead code entirely rather than commenting it out or renaming with `_`.
- **MiniMessage everywhere** — all player-facing text uses Kyori Adventure MiniMessage tags (`<red>`, `<bold>`,
  `<gradient:…>`). Never use `ChatColor`.
- **Thread safety** — `StatManager.profiles` uses `ConcurrentHashMap`; keep stat mutations on the main thread.
- **No manual registration** — never edit `plugin.yml` commands/listeners. The auto-registration system handles
  everything.

## Adding a New Stat

1. Add an entry to `Stat.kt` with `displayName`, `symbol`, `color`, `baseValue`, and `StatCategory`.
2. Create a listener in `listeners/stats/` that reads `StatManager.getStat(player, Stat.YOUR_STAT)` and applies the
   effect.
3. Optionally add skill bonuses in `SkillConfig` for the relevant skills.

## Adding a New Skill XP Source

Create a listener in `listeners/skills/` and call `SkillManager.addXP(player, Skill.YOUR_SKILL, amount)`. The manager
fires `SkillXPGainEvent` and `SkillLevelUpEvent` automatically.

## Adding a Gear Bonus to an Item

```kotlin
import net.trilleo.mc.plugins.trisurvival.stats.GearBonusReader
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import org.bukkit.persistence.PersistentDataType

val bonuses = mapOf(Stat.STRENGTH to 10.0, Stat.CRIT_CHANCE to 5.0)
val json = GearBonusReader.encodeBonuses(bonuses)
PDCUtil.set(item, GearBonusReader.STAT_BONUSES_KEY, PersistentDataType.STRING, json)
```

## Custom Item Notes

- **Icons can use any material, including blocks.** Custom items can never be placed
  (`CustomItemPlacementListener` cancels `BlockPlaceEvent`) and never feed a vanilla recipe
  (`CustomItemCraftGuardListener` blanks the vanilla craft result), so block-material icons are safe. Use
  `PluginItem.isCustom(stack)` to detect a custom item.
- **Skull items** — set `material = Material.PLAYER_HEAD` and override `texture` with a base64 skin string for a
  Hypixel-style head; the texture is applied automatically. In the `itemStack { }` DSL, use `skullTexture(base64)`.

## Adding a Custom Mob

Declare a Kotlin `object` extending `CustomMob` anywhere under `mobs/`. It is auto-registered — no manual wiring.

```kotlin
object CryptGhoul : CustomMob("crypt_ghoul") {
    override val displayName = "Crypt Ghoul"
    override val entityType = EntityType.ZOMBIE
    override val rarity = MobRarity.UNCOMMON
    override val baseXp = 15.0
    override val stats = mapOf(Stat.HEALTH to 300.0, Stat.DAMAGE to 40.0, Stat.DEFENSE to 20.0)
    override val drops = listOf(ChanceDrop(chance = 0.05) { ItemStack(Material.IRON_INGOT) })
}
```

- **Passive mobs** omit `Stat.DAMAGE` (e.g. a custom cow).
- **Abilities** — implement `MobAbility` (no-op hooks: `onSpawn`/`onTick`/`onAttack`/`onDamaged`/`onDeath`) and add it to
  `abilities`. Keep abilities stateless (a shared instance serves every mob of the definition).
- **Spawning** — `MobManager.spawn(id, location)` / `def.spawn(location)`, or `/mob spawn <id>`. To replace natural
  spawns, register a `MobSpawnRule` in `MobSpawnRegistry`; for zone spawns use `/mob zone add …`.

## Adding a Sea Creature

Extend `SeaCreature` (a `CustomMob`) under `mobs/fishing/`, set the usual mob fields, and optionally override
`onFishedUp(entity, player)` to react to the angler (e.g. aggro, leap out of the water). It auto-registers and is rolled
by `SeaCreatureRegistry` on a Sea Creature Chance proc.
