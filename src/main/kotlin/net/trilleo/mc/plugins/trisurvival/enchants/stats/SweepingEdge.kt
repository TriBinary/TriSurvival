package net.trilleo.mc.plugins.trisurvival.enchants.stats

import net.trilleo.mc.plugins.trisurvival.enchants.StatEnchant
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.stats.Stat

object SweepingEdge : StatEnchant("sweeping_edge") {
    override val displayName = "Sweeping Edge"
    override val maxLevel = 5
    override val maxTableLevel = 3
    override val applicableTypes = setOf(ItemType.SWORD)
    override val skillRequirement = 0

    override fun xpCost(level: Int): Int = level * 2

    override fun statBonuses(level: Int): Map<Stat, Double> = mapOf(
        Stat.SWEEP to 10.0 * level
    )

    override fun description(level: Int): String =
        "Grants <dark_green>+${10 * level} Sweep<gray>."
}
