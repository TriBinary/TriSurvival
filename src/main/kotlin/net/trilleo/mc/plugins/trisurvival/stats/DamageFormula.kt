package net.trilleo.mc.plugins.trisurvival.stats

import kotlin.random.Random

object DamageFormula {

    data class DamageResult(val damage: Double, val isCrit: Boolean)

    fun calculateDamage(
        baseDamage: Double,
        strength: Double,
        critChance: Double,
        critDamage: Double
    ): DamageResult {
        var damage = (5 + baseDamage) * (1 + strength / 100.0)
        val isCrit = Random.nextDouble(100.0) < critChance
        if (isCrit) {
            damage *= (1 + critDamage / 100.0)
        }
        return DamageResult(damage, isCrit)
    }

    fun reduceDamage(rawDamage: Double, defense: Double): Double =
        rawDamage / (1 + defense / 100.0)
}
