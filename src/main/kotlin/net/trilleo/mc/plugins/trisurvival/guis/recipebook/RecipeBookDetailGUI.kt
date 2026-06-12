package net.trilleo.mc.plugins.trisurvival.guis.recipebook

import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.crafting.CraftingIngredient
import net.trilleo.mc.plugins.trisurvival.crafting.CraftingRecipeRegistry
import net.trilleo.mc.plugins.trisurvival.enums.FillMode
import net.trilleo.mc.plugins.trisurvival.guis.CraftingTableGUI
import net.trilleo.mc.plugins.trisurvival.items.VanillaItemConverter
import net.trilleo.mc.plugins.trisurvival.registration.GUIManager
import net.trilleo.mc.plugins.trisurvival.registration.ItemRegistrar
import net.trilleo.mc.plugins.trisurvival.registration.PluginGUI
import net.trilleo.mc.plugins.trisurvival.utils.itemStack
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class RecipeBookDetailGUI : PluginGUI(
    id = "recipe_book_detail",
    title = MiniMessage.miniMessage().deserialize("<dark_gray>Recipe"),
    rows = 6,
    fillMode = FillMode.DARK
) {

    private val mm = MiniMessage.miniMessage()

    companion object {
        const val BACK_SLOT = 49
    }

    override fun setup(player: Player, inventory: Inventory) {
        val recipeKey = RecipeBookSession.getRecipe(player.uniqueId) ?: return
        val recipe = CraftingRecipeRegistry.customRecipe(recipeKey) ?: return

        for (i in 0 until 9) {
            val display = displayFor(recipe.shape[i]) ?: continue
            inventory.setItem(CraftingTableGUI.INPUT_SLOTS[i], display)
        }

        inventory.setItem(CraftingTableGUI.ARROW_SLOT, itemStack(Material.ARROW) {
            name("<dark_gray>➜")
            hideTooltip(true)
        })

        val result = recipe.result.clone()
        VanillaItemConverter.convert(result)
        if (recipe.transfer != null) appendTransferNote(result)
        inventory.setItem(CraftingTableGUI.RESULT_SLOT, result)

        inventory.setItem(BACK_SLOT, itemStack(Material.ARROW) {
            name("<yellow>Back")
        })
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        val player = event.whoClicked as? Player ?: return
        if (event.rawSlot == BACK_SLOT) {
            GUIManager.open(player, "recipe_book_list")
        }
    }

    private fun displayFor(ingredient: CraftingIngredient?): ItemStack? = when (ingredient) {
        null -> null
        is CraftingIngredient.VanillaMaterial -> itemStack(ingredient.material) { amount(ingredient.amount) }
        is CraftingIngredient.CustomItem -> ItemRegistrar.get(ingredient.itemId)?.create(ingredient.amount)
        is CraftingIngredient.ChoiceGroup -> itemStack(ingredient.choices.first()) {
            amount(ingredient.amount)
            lore("<gray>Any of: ${ingredient.choices.joinToString(", ") { formatMaterial(it) }}")
        }
    }

    private fun appendTransferNote(result: ItemStack) {
        val meta = result.itemMeta ?: return
        val lore = (meta.lore() ?: mutableListOf()).toMutableList()
        lore.add(mm.deserialize("<!i><light_purple>Inherits enchants from upgrade material"))
        meta.lore(lore)
        result.itemMeta = meta
    }

    private fun formatMaterial(material: Material): String =
        material.name.lowercase().split('_').joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
}
