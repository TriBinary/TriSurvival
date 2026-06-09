package net.trilleo.mc.plugins.trisurvival.crafting

import org.bukkit.inventory.ItemStack

abstract class CustomRecipe(val key: String) {
    abstract val shape: Array<CraftingIngredient?>
    abstract val result: ItemStack
    open val shaped: Boolean = true

    init {
        require(key.matches(Regex("[a-z0-9_]+"))) {
            "Recipe key must be lower-case alphanumeric with underscores: $key"
        }
    }
}
