package net.trilleo.mc.plugins.trisurvival.items.ores

import net.trilleo.mc.plugins.trisurvival.items.ItemRarity
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import org.bukkit.Material

object RubyItem : PluginItem("ruby") {

    override val displayName = "Ruby"
    override val material = Material.RED_DYE
    override val rarity = ItemRarity.RARE
}