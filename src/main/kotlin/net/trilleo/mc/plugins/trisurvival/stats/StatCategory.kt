package net.trilleo.mc.plugins.trisurvival.stats

enum class StatCategory(val displayName: String, val order: Int) {
    COMBAT("Combat", 0),
    HEALTH("Health", 1),
    MINING("Mining", 2),
    FARMING("Farming", 3),
    FORAGING("Foraging", 4),
    FISHING("Fishing", 5),
    UTILITY("Utility", 6);
}
