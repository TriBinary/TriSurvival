package net.trilleo.mc.plugins.trisurvival.guis.recipebook

import net.trilleo.mc.plugins.trisurvival.crafting.RecipeCategory
import org.bukkit.NamespacedKey
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Carries the recipe-book selection (active category and recipe) across the
 * three book GUIs, since [net.trilleo.mc.plugins.trisurvival.registration.GUIManager.open]
 * only takes a GUI id. Cleared when the player closes a book GUI.
 */
object RecipeBookSession {

    /** PDC key on a clicked category icon → [RecipeCategory.name]. */
    @JvmField
    val CATEGORY_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:recipe_category")!!

    /** PDC key on a clicked (unlocked) recipe entry → [net.trilleo.mc.plugins.trisurvival.crafting.CustomRecipe.key]. */
    @JvmField
    val RECIPE_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:recipe_key")!!

    private val categories = ConcurrentHashMap<UUID, RecipeCategory>()
    private val recipes = ConcurrentHashMap<UUID, String>()

    fun setCategory(uuid: UUID, category: RecipeCategory) {
        categories[uuid] = category
    }

    fun getCategory(uuid: UUID): RecipeCategory? = categories[uuid]

    fun setRecipe(uuid: UUID, recipeKey: String) {
        recipes[uuid] = recipeKey
    }

    fun getRecipe(uuid: UUID): String? = recipes[uuid]

    fun clear(uuid: UUID) {
        categories.remove(uuid)
        recipes.remove(uuid)
    }
}
