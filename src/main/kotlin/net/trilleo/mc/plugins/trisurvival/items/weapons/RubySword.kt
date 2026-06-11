package net.trilleo.mc.plugins.trisurvival.items.weapons

import net.trilleo.mc.plugins.trisurvival.items.ItemRarity
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import org.bukkit.Material

object RubySword : PluginItem("ruby_sword") {

    override val displayName = "Ruby Sword"
    override val material = Material.DIAMOND_SWORD
    override val rarity = ItemRarity.EPIC
    override val type = ItemType.SWORD

    override val statBonuses: Map<Stat, Double> = mapOf(
        Stat.DAMAGE to 150.0,
        Stat.STRENGTH to 75.0,
        Stat.SWING_RANGE to 1.0,
        Stat.COMBAT_WISDOM to 10.0,
    )
}