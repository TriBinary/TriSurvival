package net.trilleo.mc.plugins.trisurvival.enchants.stats

import net.trilleo.mc.plugins.trisurvival.enchants.StatEnchant
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.stats.Stat

object Efficiency : StatEnchant("efficiency") {
    override val displayName = "Efficiency"
    override val maxLevel = 5
    override val maxTableLevel = 5
    override val applicableTypes = setOf(ItemType.PICKAXE, ItemType.AXE, ItemType.HOE)
    override val skillRequirement = 0

    override fun xpCost(level: Int): Int = level * 2

    override fun statBonuses(level: Int): Map<Stat, Double> = mapOf(
        Stat.MINING_SPEED to 20.0 * level
    )

    override fun description(level: Int): String =
        "Grants <yellow>+${20 * level} Mining Speed<gray>."
}
