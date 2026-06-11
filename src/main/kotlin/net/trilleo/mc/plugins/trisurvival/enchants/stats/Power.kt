package net.trilleo.mc.plugins.trisurvival.enchants.stats

import net.trilleo.mc.plugins.trisurvival.enchants.StatEnchant
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.stats.Stat

object Power : StatEnchant("power") {
    override val displayName = "Power"
    override val maxLevel = 7
    override val maxTableLevel = 5
    override val applicableTypes = setOf(ItemType.BOW)
    override val skillRequirement = 0

    override fun xpCost(level: Int): Int = level * 2

    override fun statBonuses(level: Int): Map<Stat, Double> = mapOf(
        Stat.STRENGTH to 6.0 * level,
        Stat.DAMAGE to 6.0 * level
    )

    override fun description(level: Int): String =
        "Increases bow damage by <red>+${6 * level} Strength <gray>and <dark_red>+${6 * level} Damage<gray>."
}
