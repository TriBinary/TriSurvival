package net.trilleo.mc.plugins.trisurvival.items.armor

import net.trilleo.mc.plugins.trisurvival.items.ItemRarity
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.utils.ItemStackBuilder
import org.bukkit.Material

object DiamondNecronHelmet : PluginItem("diamond_necron_helmet") {

    override val displayName = "Necron's Helmet"
    override val material = Material.DIAMOND_HELMET
    override val rarity = ItemRarity.LEGENDARY
    override val type = ItemType.HELMET

    override val statBonuses = mapOf(
        Stat.HEALTH to 50.0,
        Stat.DEFENSE to 110.0,
        Stat.INTELLIGENCE to 50.0
    )

    override fun customize(builder: ItemStackBuilder) {
        builder.unbreakable(true)
    }
}
