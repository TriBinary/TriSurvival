package net.trilleo.mc.plugins.trisurvival.items.special

import net.trilleo.mc.plugins.trisurvival.items.ItemRarity
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.utils.ItemStackBuilder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Recombobulator3000 : PluginItem("recombobulator_3000") {

    override val displayName = "Recombobulator 3000"
    override val material = Material.HEART_OF_THE_SEA
    override val rarity = ItemRarity.LEGENDARY

    override fun customize(builder: ItemStackBuilder) {
        builder.glint(true)
    }

    fun isRecombobulator(item: ItemStack): Boolean = matches(item)
}
