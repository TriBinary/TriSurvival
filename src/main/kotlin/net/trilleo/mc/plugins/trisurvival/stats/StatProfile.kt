package net.trilleo.mc.plugins.trisurvival.stats

import java.util.*

class StatProfile(val uuid: UUID) {

    private val stats = EnumMap<Stat, Double>(Stat::class.java)

    var currentMana: Double = 0.0

    init {
        Stat.entries.forEach { stats[it] = it.baseValue }
    }

    operator fun get(stat: Stat): Double = stats[stat] ?: stat.baseValue

    operator fun set(stat: Stat, value: Double) {
        stats[stat] = value
    }

    val health: Double get() = this[Stat.HEALTH]
    val defense: Double get() = this[Stat.DEFENSE]
    val strength: Double get() = this[Stat.STRENGTH]
    val critChance: Double get() = this[Stat.CRIT_CHANCE]
    val critDamage: Double get() = this[Stat.CRIT_DAMAGE]
    val speed: Double get() = this[Stat.SPEED]
    val intelligence: Double get() = this[Stat.INTELLIGENCE]

    val maxMana: Double get() = intelligence

    fun clampMana() {
        if (currentMana > maxMana) currentMana = maxMana
    }

    fun toMap(): Map<Stat, Double> = stats.toMap()
}
