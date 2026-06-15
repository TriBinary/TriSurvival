package net.trilleo.mc.plugins.trisurvival.items.reforges

import net.trilleo.mc.plugins.trisurvival.items.ItemRarity
import net.trilleo.mc.plugins.trisurvival.reforges.HeroicReforge
import net.trilleo.mc.plugins.trisurvival.reforges.ReforgeStone
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.utils.ItemStackBuilder
import org.bukkit.Material
import org.bukkit.persistence.PersistentDataType

object HeroicReforgeStone : PluginItem("heroic_reforge_stone") {

    override val displayName = "Heroic Reforge Stone"
    override val material = Material.PRISMARINE_SHARD
    override val rarity = ItemRarity.RARE

    override fun customize(builder: ItemStackBuilder) {
        builder.glint(true)
        builder.pdc(ReforgeStone.STONE_KEY, PersistentDataType.STRING, HeroicReforge.id)
    }
}
