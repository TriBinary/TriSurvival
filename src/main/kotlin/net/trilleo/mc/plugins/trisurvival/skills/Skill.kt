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
    COMBAT("Combat", Material.DIAMOND_SWORD, 50.0, 1.15, 100),
    MINING("Mining", Material.DIAMOND_PICKAXE, 50.0, 1.15, 100),
    FARMING("Farming", Material.DIAMOND_HOE, 50.0, 1.12, 100),
    FORAGING("Foraging", Material.DIAMOND_AXE, 50.0, 1.12, 100),
    FISHING("Fishing", Material.FISHING_ROD, 50.0, 1.10, 100),
    ENCHANTING("Enchanting", Material.ENCHANTING_TABLE, 75.0, 1.18, 100),
    ALCHEMY("Alchemy", Material.BREWING_STAND, 75.0, 1.18, 100);

    fun xpForLevel(level: Int): Double = baseXP * multiplier.pow(level - 1)

    fun totalXpForLevel(level: Int): Double =
        (1..level).sumOf { xpForLevel(it) }
}
