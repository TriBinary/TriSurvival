package net.trilleo.mc.plugins.trisurvival.mobs.runtime

import net.trilleo.mc.plugins.trisurvival.stats.Stat
import java.util.EnumMap

/**
 * Resolved live stat store for a single mob instance, seeded from its definition's stat map.
 *
 * Unlike a player [net.trilleo.mc.plugins.trisurvival.stats.StatProfile], unspecified stats default
 * to `0.0` rather than the player base value — a mob only has the stats its definition grants. Values
 * are mutable so abilities can buff or debuff a mob at runtime.
 */
class MobProfile(base: Map<Stat, Double>) {

    private val stats = EnumMap<Stat, Double>(Stat::class.java)

    init {
        base.forEach { (stat, value) -> stats[stat] = value }
    }

    operator fun get(stat: Stat): Double = stats[stat] ?: 0.0

    operator fun set(stat: Stat, value: Double) {
        stats[stat] = value
    }

    val health: Double get() = this[Stat.HEALTH]
    val damage: Double get() = this[Stat.DAMAGE]
    val defense: Double get() = this[Stat.DEFENSE]
}
