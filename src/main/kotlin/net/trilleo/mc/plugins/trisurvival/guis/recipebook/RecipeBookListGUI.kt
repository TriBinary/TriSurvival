package net.trilleo.mc.plugins.trisurvival.guis.recipebook

import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.crafting.CraftingRecipeRegistry
import net.trilleo.mc.plugins.trisurvival.enums.FillMode
import net.trilleo.mc.plugins.trisurvival.items.VanillaItemConverter
import net.trilleo.mc.plugins.trisurvival.registration.GUIManager
import net.trilleo.mc.plugins.trisurvival.registration.PagedPluginGUI
import net.trilleo.mc.plugins.trisurvival.utils.PDCUtil
import net.trilleo.mc.plugins.trisurvival.utils.itemStack
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class RecipeBookListGUI : PagedPluginGUI(
    id = "recipe_book_list",
    title = MiniMessage.miniMessage().deserialize("<dark_gray>Recipe Book"),
    rows = 6,
    fillMode = FillMode.DARK
) {

    override fun backTarget(player: Player): String? = "recipe_book"

    override fun getItems(player: Player): List<ItemStack> {
        val category = RecipeBookSession.getCategory(player.uniqueId) ?: return emptyList()
        return CraftingRecipeRegistry.customRecipesByCategory(category).map { recipe ->
            if (recipe.isUnlocked(player)) {
                val display = recipe.result.clone()
                VanillaItemConverter.convert(display)
                PDCUtil.set(display, RecipeBookSession.RECIPE_KEY, PersistentDataType.STRING, recipe.key)
                display
            } else {
                val nameComp = recipe.result.itemMeta?.displayName()
                itemStack(Material.GRAY_DYE) {
                    meta { if (nameComp != null) displayName(nameComp) }
                }
            }
        }
    }

    override fun onContentClick(event: InventoryClickEvent, page: Int) {
        val player = event.whoClicked as? Player ?: return
        val recipeKey = event.currentItem?.let {
            PDCUtil.get(it, RecipeBookSession.RECIPE_KEY, PersistentDataType.STRING)
        } ?: return
        RecipeBookSession.setRecipe(player.uniqueId, recipeKey)
        GUIManager.open(player, "recipe_book_detail")
    }
}
