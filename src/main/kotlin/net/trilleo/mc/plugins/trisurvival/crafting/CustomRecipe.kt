package net.trilleo.mc.plugins.trisurvival.crafting

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

    /** Optional attribute transfer applied to the result at craft time. */
    open val transfer: RecipeTransfer? = null

    init {
        require(key.matches(Regex("[a-z0-9_]+"))) {
            "Recipe key must be lower-case alphanumeric with underscores: $key"
        }
    }

    fun isUnlocked(player: Player): Boolean {
        val skill = category.skill ?: return true
        return unlockLevel <= 0 || SkillManager.getLevel(player.uniqueId, skill) >= unlockLevel
    }
}
