package net.trilleo.mc.plugins.trisurvival.enchants.stats

import net.trilleo.mc.plugins.trisurvival.enchants.StatEnchant
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.stats.Stat

object LuckOfTheSea : StatEnchant("luck_of_the_sea") {
    override val displayName = "Luck of the Sea"
    override val maxLevel = 5
    override val maxTableLevel = 3
    override val applicableTypes = setOf(ItemType.FISHING_ROD)
    override val skillRequirement = 5

    override fun xpCost(level: Int): Int = level * 3

    override fun statBonuses(level: Int): Map<Stat, Double> = mapOf(
        Stat.TREASURE_CHANCE to 5.0 * level
    )

    override fun description(level: Int): String =
        "Grants <gold>+${5 * level}% Treasure Chance<gray> while fishing."
}
