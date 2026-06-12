package net.trilleo.mc.plugins.trisurvival.crafting

import net.trilleo.mc.plugins.trisurvival.collections.CollectionRequirement
import net.trilleo.mc.plugins.trisurvival.crafting.transfer.RecipeTransfer
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class CustomRecipe(val key: String) {
    abstract val shape: Array<CraftingIngredient?>
    abstract val result: ItemStack
    open val shaped: Boolean = true

    /** Recipe-book category this recipe is filed under. */
    open val category: RecipeCategory = RecipeCategory.MISC

    /** Level in [RecipeCategory.skill] required to unlock; 0 = always unlocked. */
    open val unlockLevel: Int = 0

    /** Collection tiers required to unlock, in addition to [unlockLevel]. */
    open val collectionRequirements: List<CollectionRequirement> = emptyList()

    /** Optional attribute transfer applied to the result at craft time. */
    open val transfer: RecipeTransfer? = null

    init {
        require(key.matches(Regex("[a-z0-9_]+"))) {
            "Recipe key must be lower-case alphanumeric with underscores: $key"
        }
    }

    fun isUnlocked(player: Player): Boolean =
        skillRequirementMet(player) && collectionRequirements.all { it.isMet(player) }

    private fun skillRequirementMet(player: Player): Boolean {
        val skill = category.skill ?: return true
        return unlockLevel <= 0 || SkillManager.getLevel(player.uniqueId, skill) >= unlockLevel
    }

    /**
     * The recipe's unlock requirements as display lines (skill level first, then collection tiers),
     * each flagged with whether [player] currently meets it. Used by the recipe book.
     */
    fun requirementLines(player: Player): List<RequirementLine> {
        val lines = ArrayList<RequirementLine>()
        val skill = category.skill
        if (skill != null && unlockLevel > 0) {
            val met = SkillManager.getLevel(player.uniqueId, skill) >= unlockLevel
            lines.add(RequirementLine("${skill.displayName} Level $unlockLevel", met))
        }
        for (requirement in collectionRequirements) {
            lines.add(RequirementLine(requirement.describe(), requirement.isMet(player)))
        }
        return lines
    }
}
