package net.trilleo.mc.plugins.trisurvival.crafting

import net.trilleo.mc.plugins.trisurvival.skills.Skill
import org.bukkit.Material

/**
 * The eight recipe-book categories: one per [Skill] plus a catch-all [MISC].
 * A category's [skill] gates its recipes via [CustomRecipe.unlockLevel]; [MISC]
 * has no skill and is therefore always unlocked.
 */
enum class RecipeCategory(
    val displayName: String,
    val icon: Material,
    val skill: Skill?
) {
    COMBAT("Combat", Skill.COMBAT.material, Skill.COMBAT),
    MINING("Mining", Skill.MINING.material, Skill.MINING),
    FARMING("Farming", Skill.FARMING.material, Skill.FARMING),
    FORAGING("Foraging", Skill.FORAGING.material, Skill.FORAGING),
    FISHING("Fishing", Skill.FISHING.material, Skill.FISHING),
    ENCHANTING("Enchanting", Skill.ENCHANTING.material, Skill.ENCHANTING),
    ALCHEMY("Alchemy", Skill.ALCHEMY.material, Skill.ALCHEMY),
    MISC("Misc", Material.CHEST, null)
}
