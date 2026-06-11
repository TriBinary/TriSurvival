package net.trilleo.mc.plugins.trisurvival.enchants.stats

import net.trilleo.mc.plugins.trisurvival.enchants.StatEnchant
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.stats.Stat

object Fortune : StatEnchant("fortune") {
    override val displayName = "Fortune"
    override val maxLevel = 5
    override val maxTableLevel = 3
    override val applicableTypes = setOf(ItemType.PICKAXE)
    override val skillRequirement = 5

    override fun xpCost(level: Int): Int = level * 3

    override fun statBonuses(level: Int): Map<Stat, Double> = mapOf(
        Stat.MINING_FORTUNE to 15.0 * level
    )

    override fun description(level: Int): String =
        "Grants <gold>+${15 * level} Mining Fortune<gray>."
}
