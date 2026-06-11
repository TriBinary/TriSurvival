package net.trilleo.mc.plugins.trisurvival.enchants.ultimate

import net.trilleo.mc.plugins.trisurvival.enchants.StatEnchant
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.stats.Stat

object OneForAll : StatEnchant("one_for_all") {
    override val displayName = "One For All"
    override val maxLevel = 1
    override val maxTableLevel = 0
    override val applicableTypes = setOf(ItemType.SWORD, ItemType.AXE)
    override val skillRequirement = 25
    override val ultimate = true

    override fun xpCost(level: Int): Int = 30

    override fun statBonuses(level: Int): Map<Stat, Double> = mapOf(
        Stat.DAMAGE to 500.0
    )

    override fun description(level: Int): String =
        "Concentrates all power: <dark_red>+500 Damage<gray>. Only one Ultimate per item."
}
