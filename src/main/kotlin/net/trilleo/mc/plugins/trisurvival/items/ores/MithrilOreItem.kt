package net.trilleo.mc.plugins.trisurvival.items.ores

import net.trilleo.mc.plugins.trisurvival.items.ItemRarity
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import org.bukkit.Material

object MithrilOreItem : PluginItem("mithril_ore") {

    override val displayName = "Mithril"
    override val material = Material.PRISMARINE_CRYSTALS
    override val rarity = ItemRarity.UNCOMMON
}
