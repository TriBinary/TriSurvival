package net.trilleo.mc.plugins.trisurvival.collections.definitions

import net.trilleo.mc.plugins.trisurvival.collections.Collection
import net.trilleo.mc.plugins.trisurvival.collections.CollectionCategory
import net.trilleo.mc.plugins.trisurvival.collections.CollectionProvider
import net.trilleo.mc.plugins.trisurvival.collections.CollectionSource.Vanilla
import net.trilleo.mc.plugins.trisurvival.collections.CollectionTiers
import org.bukkit.Material

/** Built-in Foraging collections — one per wood type, keyed by log. Logs use the placed filter. */
object ForagingCollections : CollectionProvider() {

    private fun tiers() = CollectionTiers.geometric(base = 40, growth = 2.0, cap = 9)

    private fun wood(id: String, name: String, log: Material) =
        Collection(id, name, log, CollectionCategory.FORAGING, listOf(Vanilla(log)), tiers(), placedFilter = true)

    override fun collections(): List<Collection> = listOf(
        wood("oak_log", "Oak", Material.OAK_LOG),
        wood("birch_log", "Birch", Material.BIRCH_LOG),
        wood("spruce_log", "Spruce", Material.SPRUCE_LOG),
        wood("jungle_log", "Jungle", Material.JUNGLE_LOG),
        wood("acacia_log", "Acacia", Material.ACACIA_LOG),
        wood("dark_oak_log", "Dark Oak", Material.DARK_OAK_LOG),
        wood("mangrove_log", "Mangrove", Material.MANGROVE_LOG),
        wood("cherry_log", "Cherry", Material.CHERRY_LOG),
        wood("crimson_stem", "Crimson", Material.CRIMSON_STEM),
        wood("warped_stem", "Warped", Material.WARPED_STEM)
    )
}
