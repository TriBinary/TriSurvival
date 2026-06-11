package net.trilleo.mc.plugins.trisurvival.enchants.stats

import net.trilleo.mc.plugins.trisurvival.enchants.StatEnchant
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.stats.Stat

object Growth : StatEnchant("growth") {
    override val displayName = "Growth"
    override val maxLevel = 6
    override val maxTableLevel = 3
    override val applicableTypes = setOf(
        ItemType.HELMET, ItemType.CHESTPLATE, ItemType.LEGGINGS, ItemType.BOOTS
    )
    override val skillRequirement = 0

    override fun xpCost(level: Int): Int = level * 2

    override fun statBonuses(level: Int): Map<Stat, Double> = mapOf(
        Stat.HEALTH to 15.0 * level
    )

    override fun description(level: Int): String =
        "Grants <red>+${15 * level} Health<gray>."
}
