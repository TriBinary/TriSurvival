package net.trilleo.mc.plugins.trisurvival.collections

import net.trilleo.mc.plugins.trisurvival.skills.Skill
import org.bukkit.Material

/**
 * Top-level grouping a [Collection] is filed under in the collections menu, mirroring the five
 * gathering skills. [skill] is the related skill (purely informational for now).
 */
enum class CollectionCategory(
    val displayName: String,
    val icon: Material,
    val skill: Skill?
) {
    COMBAT("Combat", Material.DIAMOND_SWORD, Skill.COMBAT),
    MINING("Mining", Material.IRON_PICKAXE, Skill.MINING),
    FARMING("Farming", Material.GOLDEN_HOE, Skill.FARMING),
    FORAGING("Foraging", Material.OAK_SAPLING, Skill.FORAGING),
    FISHING("Fishing", Material.FISHING_ROD, Skill.FISHING)
}
