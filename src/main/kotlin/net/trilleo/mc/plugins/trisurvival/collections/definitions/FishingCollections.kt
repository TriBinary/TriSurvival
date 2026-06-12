package net.trilleo.mc.plugins.trisurvival.collections.definitions

import net.trilleo.mc.plugins.trisurvival.collections.Collection
import net.trilleo.mc.plugins.trisurvival.collections.CollectionCategory
import net.trilleo.mc.plugins.trisurvival.collections.CollectionProvider
import net.trilleo.mc.plugins.trisurvival.collections.CollectionSource.Vanilla
import net.trilleo.mc.plugins.trisurvival.collections.CollectionTiers
import org.bukkit.Material

/** Built-in Fishing collections, keyed by the item reeled in. */
object FishingCollections : CollectionProvider() {

    private fun tiers() = CollectionTiers.geometric(base = 20, growth = 2.0, cap = 9)

    private fun fishing(id: String, name: String, icon: Material) =
        Collection(id, name, icon, CollectionCategory.FISHING, listOf(Vanilla(icon)), tiers())

    override fun collections(): List<Collection> = listOf(
        fishing("cod", "Cod", Material.COD),
        fishing("salmon", "Salmon", Material.SALMON),
        fishing("pufferfish", "Pufferfish", Material.PUFFERFISH),
        fishing("tropical_fish", "Tropical Fish", Material.TROPICAL_FISH)
    )
}
