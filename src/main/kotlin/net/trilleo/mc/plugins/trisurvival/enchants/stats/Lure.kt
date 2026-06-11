package net.trilleo.mc.plugins.trisurvival.enchants.stats

import net.trilleo.mc.plugins.trisurvival.enchants.StatEnchant
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.stats.Stat

object Lure : StatEnchant("lure") {
    override val displayName = "Lure"
    override val maxLevel = 5
    override val maxTableLevel = 3
    override val applicableTypes = setOf(ItemType.FISHING_ROD)
    override val skillRequirement = 0

    override fun xpCost(level: Int): Int = level * 2

    override fun statBonuses(level: Int): Map<Stat, Double> = mapOf(
        Stat.FISHING_SPEED to 15.0 * level
    )

    override fun description(level: Int): String =
        "Grants <aqua>+${15 * level} Fishing Speed<gray>."
}
