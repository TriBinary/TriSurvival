<h1 align="center">
  TriSurvival
</h1>

<p align="center">
  A Minecraft Paper plugin delivering a full RPG progression system — skills, stats, gear bonuses, custom fishing, and more.
</p>

---

## Features

- **7 Skills** — Combat, Mining, Farming, Foraging, Fishing, Enchanting, Alchemy — each with a configurable XP curve (up to level 60) and per-level stat bonuses.
- **31 Stats** — Organized across Combat, Health, Utility, Mining, Farming, Foraging, and Fishing categories. Stats stack from three sources: base values, skill bonuses, and gear bonuses.
- **Gear Bonus System** — Items carry stat bonuses encoded in their Persistent Data Container. Equipping or removing gear automatically recalculates the player's stat profile.
- **Sea Creature Framework** — Extensible system for custom fishing mobs with rarity tiers (registered via `SeaCreatureRegistry`).
- **Custom Crafting** — GUI-based crafting table with multi-ingredient recipe support.
- **Data Persistence** — Per-player and server-wide JSON storage with typed getters and setters.
- **Auto-Registration** — Commands, listeners, GUIs, tasks, items, and recipes are discovered and wired up automatically at startup — no `plugin.yml` edits needed.

## Requirements

| Dependency | Version |
|:-----------|:--------|
| Paper | 1.21+ |
| Java | 25+ |

## Building

```powershell
./gradlew build
```

The compiled JAR is placed in `build/libs/`. During development, `./gradlew build` also copies the JAR to `run/plugins/` for the local test server.

## Commands

All commands are sub-commands of `/trisurvival` (alias `/ts`).

| Command | Description |
|:--------|:------------|
| `/ts help` | List all available commands |
| `/ts skills` | View your skill levels and XP progress |
| `/ts stats` | View your current stat profile |
| `/ts reload` | Reload the plugin configuration (OP only) |
| `/ts debug skill` | Debug skill data for your player (OP only) |

## Stats Reference

| Stat | Symbol | Category | Base |
|:-----|:-------|:---------|-----:|
| Health | ❤ | Health | 100 |
| Defense | ❈ | Combat | 0 |
| Strength | ❁ | Combat | 0 |
| Crit Chance | ☣ | Combat | 30% |
| Crit Damage | ☠ | Combat | 50% |
| Attack Speed | ⚔ | Combat | 0 |
| Ferocity | ⫽ | Combat | 0 |
| Swing Range | Ⓢ | Combat | 0 |
| Health Regen | ❣ | Health | 0 |
| Vitality | ♨ | Health | 0 |
| Absorption | ❤ | Health | 0 |
| Speed | ✦ | Utility | 100 |
| Intelligence | ✎ | Utility | 100 |
| Respiration | ⚶ | Utility | 0 |
| Mining Speed | ⛏ | Mining | 0 |
| Mining Spread | ▚ | Mining | 0 |
| Mining Fortune | ☘ | Mining | 0 |
| Farming Fortune | ☘ | Farming | 0 |
| Sweep | ∮ | Foraging | 0 |
| Foraging Fortune | ☘ | Foraging | 0 |
| Fishing Speed | ☂ | Fishing | 0 |
| Sea Creature Chance | α | Fishing | 0 |
| Treasure Chance | ⛃ | Fishing | 0 |
| Double Hook | ⚓ | Fishing | 0 |
| Combat/Mining/Farming/Foraging/Fishing/Enchanting/Alchemy Wisdom | ☯ | Utility | 0 |

## Skills Reference

| Skill | Material | Base XP | XP Multiplier | Max Level |
|:------|:---------|--------:|--------------:|----------:|
| Combat | Diamond Sword | 50 | 1.15× | 60 |
| Mining | Diamond Pickaxe | 50 | 1.15× | 60 |
| Farming | Diamond Hoe | 50 | 1.12× | 60 |
| Foraging | Diamond Axe | 50 | 1.12× | 60 |
| Fishing | Fishing Rod | 50 | 1.10× | 60 |
| Enchanting | Enchanting Table | 75 | 1.18× | 60 |
| Alchemy | Brewing Stand | 75 | 1.18× | 60 |

XP required for each level follows the formula: `baseXP × multiplier^(level − 1)`.

## Developer Documentation

Full development guides are in the `docs/` directory:

- [DEVELOPER_GUIDE.md](docs/DEVELOPER_GUIDE.md) — How to add commands, listeners, GUIs, tasks, items, recipes, stats, skills, and sea creatures.
- [UTILITY_GUIDE.md](docs/UTILITY_GUIDE.md) — Reference for all utility helpers (`itemStack` DSL, `MessageUtil`, `PDCUtil`, etc.).
- [COMMIT_STRUCTURE.md](docs/COMMIT_STRUCTURE.md) — Commit message conventions.

For AI-assisted development, see [CLAUDE.md](CLAUDE.md).
