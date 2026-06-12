package net.trilleo.mc.plugins.trisurvival.collections.definitions

import net.trilleo.mc.plugins.trisurvival.collections.Collection
import net.trilleo.mc.plugins.trisurvival.collections.CollectionCategory
import net.trilleo.mc.plugins.trisurvival.collections.CollectionProvider
import net.trilleo.mc.plugins.trisurvival.collections.CollectionSource
import net.trilleo.mc.plugins.trisurvival.collections.CollectionSource.Vanilla
import net.trilleo.mc.plugins.trisurvival.collections.CollectionTiers
import org.bukkit.Material

/** Built-in Farming collections, keyed by harvested item. Placeable crops use the placed filter. */
object FarmingCollections : CollectionProvider() {

    private fun tiers() = CollectionTiers.geometric(base = 40, growth = 2.0, cap = 9)

    private fun farming(
        id: String,
        name: String,
        icon: Material,
        sources: List<CollectionSource>,
        placedFilter: Boolean = false
    ) = Collection(id, name, icon, CollectionCategory.FARMING, sources, tiers(), placedFilter)

    override fun collections(): List<Collection> = listOf(
        farming("wheat", "Wheat", Material.WHEAT, listOf(Vanilla(Material.WHEAT))),
        farming("carrot", "Carrot", Material.CARROT, listOf(Vanilla(Material.CARROT))),
        farming("potato", "Potato", Material.POTATO, listOf(Vanilla(Material.POTATO))),
        farming("beetroot", "Beetroot", Material.BEETROOT, listOf(Vanilla(Material.BEETROOT))),
        farming("nether_wart", "Nether Wart", Material.NETHER_WART, listOf(Vanilla(Material.NETHER_WART))),
        farming("cocoa", "Cocoa Beans", Material.COCOA_BEANS, listOf(Vanilla(Material.COCOA_BEANS))),
        farming("sweet_berries", "Sweet Berries", Material.SWEET_BERRIES, listOf(Vanilla(Material.SWEET_BERRIES))),
        farming("pumpkin", "Pumpkin", Material.PUMPKIN, listOf(Vanilla(Material.PUMPKIN)), placedFilter = true),
        farming("melon", "Melon", Material.MELON_SLICE, listOf(Vanilla(Material.MELON_SLICE)), placedFilter = true),
        farming("sugar_cane", "Sugar Cane", Material.SUGAR_CANE, listOf(Vanilla(Material.SUGAR_CANE)), placedFilter = true),
        farming("cactus", "Cactus", Material.CACTUS, listOf(Vanilla(Material.CACTUS)), placedFilter = true),
        farming("bamboo", "Bamboo", Material.BAMBOO, listOf(Vanilla(Material.BAMBOO)), placedFilter = true),
        farming("red_mushroom", "Red Mushroom", Material.RED_MUSHROOM, listOf(Vanilla(Material.RED_MUSHROOM)), placedFilter = true),
        farming("brown_mushroom", "Brown Mushroom", Material.BROWN_MUSHROOM, listOf(Vanilla(Material.BROWN_MUSHROOM)), placedFilter = true)
    )
}
