<h1 align="center">
  TriSurvival
</h1>

A Paper (Minecraft) plugin built with Kotlin. It comes with an auto-registration system for
commands, listeners, permissions, GUIs and tasks — just extend a base class, drop it in the right package, and the
plugin handles the rest.

**Author:** Trilleo

## Project Structure

```
src/main/kotlin/net/trilleo/mc/plugins/trisurvival/
├── Main.kt                  # Plugin entry point
├── commands/                # Auto-registered commands (extend PluginCommand)
├── config/                  # Typed configuration wrapper (PluginConfig)
├── data/                    # JSON-persisted player and server data (PlayerData, ServerData)
├── enums/                   # Plugin-wide enums (e.g. FillMode, DisplayLocation)
├── guis/                    # Auto-registered GUIs (extend PluginGUI)
├── items/                   # Plugin-wide custom items (extend PluginItem)
├── listeners/               # Auto-registered listeners (implement Listener)
├── recipes/                 # Auto-registered recipes (implement PluginRecipe)
├── registration/            # Auto-registration framework
├── tasks/                   # Auto-registered tasks (extend PluginTask)
└── utils/                   # Utility helpers (e.g. ItemStack DSL)
```

See [`docs/DEVELOPER_GUIDE.md`](docs/DEVELOPER_GUIDE.md) for detailed instructions on creating commands, listeners,
GUIs, working with the configuration system, and managing player and server data.

See [`docs/UTILITY_GUIDE.md`](docs/UTILITY_GUIDE.md) for documentation on the built-in utility helpers such as
the `itemStack` DSL builder and `CountdownUtil`.

See [`docs/DEVELOPER_GUIDE.md`](docs/DEVELOPER_GUIDE.md#enums) for documentation on plugin-wide enums such as
`DisplayLocation` and `FillMode`.
