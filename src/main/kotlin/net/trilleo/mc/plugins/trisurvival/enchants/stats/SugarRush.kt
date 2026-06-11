package net.trilleo.mc.plugins.trisurvival.enchants.stats

import net.trilleo.mc.plugins.trisurvival.enchants.StatEnchant
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.stats.Stat

object SugarRush : StatEnchant("sugar_rush") {
    override val displayName = "Sugar Rush"
    override val maxLevel = 3
    override val maxTableLevel = 3
    override val applicableTypes = setOf(ItemType.BOOTS)
    override val skillRequirement = 5

    override fun xpCost(level: Int): Int = level * 3

    override fun statBonuses(level: Int): Map<Stat, Double> = mapOf(
        Stat.SPEED to 2.0 * level
    )

    override fun description(level: Int): String =
        "Grants <white>+${2 * level} Speed<gray>."
}
