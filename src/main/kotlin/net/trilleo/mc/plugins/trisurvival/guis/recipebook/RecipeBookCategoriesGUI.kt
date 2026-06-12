package net.trilleo.mc.plugins.trisurvival.guis.recipebook

import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.crafting.CraftingRecipeRegistry
import net.trilleo.mc.plugins.trisurvival.crafting.RecipeCategory
import net.trilleo.mc.plugins.trisurvival.enums.FillMode
import net.trilleo.mc.plugins.trisurvival.registration.GUIManager
import net.trilleo.mc.plugins.trisurvival.registration.PluginGUI
import net.trilleo.mc.plugins.trisurvival.utils.itemStack
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.persistence.PersistentDataType

class RecipeBookCategoriesGUI : PluginGUI(
    id = "recipe_book",
    title = MiniMessage.miniMessage().deserialize("<dark_gray>Recipe Book"),
    rows = 5,
    fillMode = FillMode.DARK
) {

    private val categorySlots = mapOf(
        RecipeCategory.COMBAT to 10,
        RecipeCategory.MINING to 11,
        RecipeCategory.FARMING to 12,
        RecipeCategory.FORAGING to 13,
        RecipeCategory.FISHING to 14,
        RecipeCategory.ENCHANTING to 15,
        RecipeCategory.ALCHEMY to 16,
        RecipeCategory.MISC to 22
    )

    override fun setup(player: Player, inventory: Inventory) {
        for ((category, slot) in categorySlots) {
            val count = CraftingRecipeRegistry.customRecipesByCategory(category).size
            inventory.setItem(slot, itemStack(category.icon) {
                name("<yellow><bold>${category.displayName}")
                lore("<gray>${count} recipe${if (count == 1) "" else "s"}", "", "<yellow>Click to browse")
                pdc(RecipeBookSession.CATEGORY_KEY, PersistentDataType.STRING, category.name)
            })
        }
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        val player = event.whoClicked as? Player ?: return
        val name = event.currentItem?.itemMeta
            ?.persistentDataContainer?.get(RecipeBookSession.CATEGORY_KEY, PersistentDataType.STRING)
            ?: return
        val category = runCatching { RecipeCategory.valueOf(name) }.getOrNull() ?: return
        RecipeBookSession.setCategory(player.uniqueId, category)
        GUIManager.open(player, "recipe_book_list")
    }
}
