package net.trilleo.mc.plugins.trisurvival.stats

import java.util.*

class StatProfile(val uuid: UUID) {

    private val stats = EnumMap<Stat, Double>(Stat::class.java)

    var currentMana: Double = 0.0
    var currentHealth: Double = 100.0

    /** The per-source breakdown captured on the last recalculation; drives the stats GUI. */
    var breakdown: StatBreakdown = StatBreakdown(emptyList())

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
    val healthRegen: Double get() = this[Stat.HEALTH_REGEN]
    val vitality: Double get() = this[Stat.VITALITY]
    val absorption: Double get() = this[Stat.ABSORPTION]
    val ferocity: Double get() = this[Stat.FEROCITY]
    val damage: Double get() = this[Stat.DAMAGE]

    val maxMana: Double get() = intelligence

    fun clampMana() {
        if (currentMana > maxMana) currentMana = maxMana
    }

    fun clampHealth() {
        currentHealth = currentHealth.coerceIn(0.0, health)
    }

    val healthFraction: Double get() = if (health > 0) currentHealth / health else 0.0

    fun toMap(): Map<Stat, Double> = stats.toMap()
}
