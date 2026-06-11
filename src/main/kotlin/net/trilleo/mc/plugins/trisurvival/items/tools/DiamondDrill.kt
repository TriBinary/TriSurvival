package net.trilleo.mc.plugins.trisurvival.items.tools

import net.trilleo.mc.plugins.trisurvival.items.ItemRarity
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.utils.ItemStackBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

object DiamondDrill : PluginItem("diamond_drill") {

    override val displayName = "Diamond Drill"
    override val material = Material.DIAMOND_PICKAXE
    override val rarity = ItemRarity.EPIC
    override val type = ItemType.PICKAXE

    override val statBonuses = mapOf(
        Stat.MINING_SPEED to 200.0,
        Stat.MINING_FORTUNE to 50.0
    )

    override fun customize(builder: ItemStackBuilder) {
        builder.enchant(Enchantment.EFFICIENCY, 5)
        builder.unbreakable(true)
    }
}
