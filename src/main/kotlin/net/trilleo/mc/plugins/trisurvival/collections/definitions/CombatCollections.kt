package net.trilleo.mc.plugins.trisurvival.collections.definitions

import net.trilleo.mc.plugins.trisurvival.collections.Collection
import net.trilleo.mc.plugins.trisurvival.collections.CollectionCategory
import net.trilleo.mc.plugins.trisurvival.collections.CollectionProvider
import net.trilleo.mc.plugins.trisurvival.collections.CollectionSource.Vanilla
import net.trilleo.mc.plugins.trisurvival.collections.CollectionTiers
import org.bukkit.Material

/** Built-in Combat collections, keyed by mob drops (counted when a player kills the mob). */
object CombatCollections : CollectionProvider() {

    private fun tiers() = CollectionTiers.geometric(base = 50, growth = 2.0, cap = 9)

    private fun combat(id: String, name: String, drop: Material) =
        Collection(id, name, drop, CollectionCategory.COMBAT, listOf(Vanilla(drop)), tiers())

    override fun collections(): List<Collection> = listOf(
        combat("rotten_flesh", "Rotten Flesh", Material.ROTTEN_FLESH),
        combat("bone", "Bone", Material.BONE),
        combat("string", "String", Material.STRING),
        combat("spider_eye", "Spider Eye", Material.SPIDER_EYE),
        combat("gunpowder", "Gunpowder", Material.GUNPOWDER),
        combat("ender_pearl", "Ender Pearl", Material.ENDER_PEARL),
        combat("blaze_rod", "Blaze Rod", Material.BLAZE_ROD),
        combat("slimeball", "Slimeball", Material.SLIME_BALL),
        combat("ghast_tear", "Ghast Tear", Material.GHAST_TEAR),
        combat("magma_cream", "Magma Cream", Material.MAGMA_CREAM),
        combat("leather", "Leather", Material.LEATHER),
        combat("feather", "Feather", Material.FEATHER),
        combat("phantom_membrane", "Phantom Membrane", Material.PHANTOM_MEMBRANE)
    )
}
