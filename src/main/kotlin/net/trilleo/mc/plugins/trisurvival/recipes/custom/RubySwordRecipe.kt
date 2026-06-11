package net.trilleo.mc.plugins.trisurvival.recipes.custom

import net.trilleo.mc.plugins.trisurvival.crafting.CraftingIngredient
import net.trilleo.mc.plugins.trisurvival.crafting.CustomRecipe
import net.trilleo.mc.plugins.trisurvival.items.weapons.RubySword
import org.bukkit.Material

object RubySwordRecipe : CustomRecipe("ruby_sword") {

    // 3x3 crafting grid represented as a flat array (0..8). Vanilla sword uses
    // middle column: indices 1,4 and stick at index 7. Each ruby slot here
    // requires 2 rubies (amount = 2) so the recipe consumes 4 rubies total.
    override val shape: Array<CraftingIngredient?> = arrayOf(
        null,
        CraftingIngredient.CustomItem("ruby", amount = 2),
        null,

        null,
        CraftingIngredient.CustomItem("ruby", amount = 2),
        null,

        null,
        CraftingIngredient.VanillaMaterial(Material.STICK),
        null
    )

    override val result = RubySword.create()
}