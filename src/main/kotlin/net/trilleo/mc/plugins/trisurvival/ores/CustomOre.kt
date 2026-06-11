package net.trilleo.mc.plugins.trisurvival.ores

import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.World
import kotlin.random.Random

/**
 * A developer-defined ore that behaves like a first-class plugin ore: it has its own block strength
 * (mining time), drops a custom item, grants Mining skill XP and vanilla XP orbs, and participates in
 * Mining Fortune and Mining Spread.
 *
 * A custom ore is represented in the world by a plain vanilla block ([representingBlock], e.g.
 * `BLUE_WOOL` for mithril). Its identity is recorded in chunk PDC by [CustomOres] so that breaking it
 * is distinguishable from a player-placed block of the same material.
 *
 * Subclasses are auto-discovered by [CustomOreRegistry] anywhere inside the `ores` package. Each must
 * be a Kotlin `object`, or declare a no-arg / `JavaPlugin` constructor.
 */
abstract class CustomOre(val id: String) {

    /** The vanilla block placed in the world to represent this ore. */
    abstract val representingBlock: Material

    /** Block strength feeding the mining-time formula (see `BlockStrength`). */
    abstract val blockStrength: Int

    /** ID of the custom item dropped on break (resolved via `ItemRegistrar`). `null` drops nothing. */
    open val dropItemId: String? = null

    /** Vanilla item drop. `null` drops nothing. */
    open val dropVanillaItem: Material? = null

    /** Number of drop items produced before Mining Fortune is applied. */
    open val baseDropAmount: Int = 1

    /** Mining skill XP granted per block broken. */
    open val skillXp: Double = 0.0

    /** Vanilla experience orbs dropped per block broken. */
    open val expOrbDrop: Int = 0

    /** When `true`, a pickaxe is required to obtain any drops from this ore. */
    open val requiresPickaxe: Boolean = false

    /** Minimum pickaxe tier required for drops; below this the ore breaks but drops nothing. */
    open val minToolTier: ToolTier = ToolTier.NONE

    /**
     * World-generation hook. Invoked once per freshly generated chunk with a live [chunk] and a
     * deterministically seeded [random]. Place ore blocks via [CustomOres.place] so they are tagged
     * as this ore. The default implementation generates nothing.
     */
    open fun generate(chunk: Chunk, world: World, random: Random) {}
}
