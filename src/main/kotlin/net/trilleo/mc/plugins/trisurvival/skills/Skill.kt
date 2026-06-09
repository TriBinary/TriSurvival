package net.trilleo.mc.plugins.trisurvival.skills

import org.bukkit.Material
import kotlin.math.pow

enum class Skill(
    val displayName: String,
    val material: Material,
    val baseXP: Double,
    val multiplier: Double,
    val maxLevel: Int
) {
    COMBAT("Combat", Material.DIAMOND_SWORD, 50.0, 1.15, 60),
    MINING("Mining", Material.DIAMOND_PICKAXE, 50.0, 1.15, 60),
    FARMING("Farming", Material.DIAMOND_HOE, 50.0, 1.12, 60),
    FORAGING("Foraging", Material.DIAMOND_AXE, 50.0, 1.12, 60),
    FISHING("Fishing", Material.FISHING_ROD, 50.0, 1.10, 60),
    ENCHANTING("Enchanting", Material.ENCHANTING_TABLE, 75.0, 1.18, 60),
    ALCHEMY("Alchemy", Material.BREWING_STAND, 75.0, 1.18, 60);

    fun xpForLevel(level: Int): Double = baseXP * multiplier.pow(level - 1)

    fun totalXpForLevel(level: Int): Double =
        (1..level).sumOf { xpForLevel(it) }
}
