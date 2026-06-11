package net.trilleo.mc.plugins.trisurvival.enchants.stats

import net.trilleo.mc.plugins.trisurvival.enchants.StatEnchant
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.stats.Stat

object Respiration : StatEnchant("respiration") {
    override val displayName = "Respiration"
    override val maxLevel = 3
    override val maxTableLevel = 3
    override val applicableTypes = setOf(ItemType.HELMET)
    override val skillRequirement = 0

    override fun xpCost(level: Int): Int = level * 2

    override fun statBonuses(level: Int): Map<Stat, Double> = mapOf(
        Stat.RESPIRATION to 1.0 * level
    )

    override fun description(level: Int): String =
        "Grants <aqua>+${level} Respiration<gray>, extending your breath underwater."
}
