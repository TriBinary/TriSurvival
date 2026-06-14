package net.trilleo.mc.plugins.trisurvival.stats

import org.bukkit.Material

enum class StatCategory(val displayName: String, val order: Int, val icon: Material) {
    COMBAT("Combat", 0, Material.DIAMOND_SWORD),
    HEALTH("Health", 1, Material.GOLDEN_APPLE),
    MINING("Mining", 2, Material.IRON_PICKAXE),
    FARMING("Farming", 3, Material.WHEAT),
    FORAGING("Foraging", 4, Material.OAK_SAPLING),
    FISHING("Fishing", 5, Material.FISHING_ROD),
    UTILITY("Utility", 6, Material.COMPASS);
}
