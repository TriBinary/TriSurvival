package net.trilleo.mc.plugins.trisurvival.collections.definitions

import net.trilleo.mc.plugins.trisurvival.collections.Collection
import net.trilleo.mc.plugins.trisurvival.collections.CollectionCategory
import net.trilleo.mc.plugins.trisurvival.collections.CollectionProvider
import net.trilleo.mc.plugins.trisurvival.collections.CollectionSource.Custom
import net.trilleo.mc.plugins.trisurvival.collections.CollectionSource.Vanilla
import net.trilleo.mc.plugins.trisurvival.collections.CollectionTiers
import org.bukkit.Material

/**
 * Built-in Mining collections, keyed by the item a block drops (so deepslate/stone variants merge
 * automatically). Also files the plugin's custom Mithril and Ruby ores here as the example of a
 * developer-defined, custom-item-sourced collection.
 */
object MiningCollections : CollectionProvider() {

    private fun tiers() = CollectionTiers.geometric(base = 50, growth = 2.0, cap = 9)

    private fun mining(
        id: String,
        name: String,
        icon: Material,
        sources: List<net.trilleo.mc.plugins.trisurvival.collections.CollectionSource>,
        placedFilter: Boolean = false
    ) = Collection(id, name, icon, CollectionCategory.MINING, sources, tiers(), placedFilter)

    override fun collections(): List<Collection> = listOf(
        mining("cobblestone", "Cobblestone", Material.COBBLESTONE, listOf(Vanilla(Material.COBBLESTONE)), placedFilter = true),
        mining("coal", "Coal", Material.COAL, listOf(Vanilla(Material.COAL))),
        mining("iron", "Iron", Material.RAW_IRON, listOf(Vanilla(Material.RAW_IRON))),
        mining("gold", "Gold", Material.RAW_GOLD, listOf(Vanilla(Material.RAW_GOLD))),
        mining("copper", "Copper", Material.RAW_COPPER, listOf(Vanilla(Material.RAW_COPPER))),
        mining("redstone", "Redstone", Material.REDSTONE, listOf(Vanilla(Material.REDSTONE))),
        mining("lapis", "Lapis Lazuli", Material.LAPIS_LAZULI, listOf(Vanilla(Material.LAPIS_LAZULI))),
        mining("emerald", "Emerald", Material.EMERALD, listOf(Vanilla(Material.EMERALD))),
        mining("diamond", "Diamond", Material.DIAMOND, listOf(Vanilla(Material.DIAMOND))),
        mining("nether_quartz", "Nether Quartz", Material.QUARTZ, listOf(Vanilla(Material.QUARTZ))),
        mining("netherite", "Netherite Scrap", Material.ANCIENT_DEBRIS, listOf(Vanilla(Material.ANCIENT_DEBRIS))),
        mining("glowstone", "Glowstone", Material.GLOWSTONE_DUST, listOf(Vanilla(Material.GLOWSTONE_DUST))),
        mining("obsidian", "Obsidian", Material.OBSIDIAN, listOf(Vanilla(Material.OBSIDIAN)), placedFilter = true),
        mining("gravel", "Gravel", Material.GRAVEL, listOf(Vanilla(Material.GRAVEL)), placedFilter = true),
        mining("sand", "Sand", Material.SAND, listOf(Vanilla(Material.SAND)), placedFilter = true),
        mining("end_stone", "End Stone", Material.END_STONE, listOf(Vanilla(Material.END_STONE)), placedFilter = true),
        mining("netherrack", "Netherrack", Material.NETHERRACK, listOf(Vanilla(Material.NETHERRACK)), placedFilter = true),

        // Example: custom-item-sourced collections for the plugin's own ores.
        mining("mithril", "Mithril", Material.PRISMARINE_CRYSTALS, listOf(Custom("mithril_ore"))),
        mining("ruby", "Ruby", Material.RED_DYE, listOf(Custom("ruby")))
    )
}
