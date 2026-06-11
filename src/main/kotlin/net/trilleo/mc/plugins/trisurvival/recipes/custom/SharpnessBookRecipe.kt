package net.trilleo.mc.plugins.trisurvival.recipes.custom

import net.trilleo.mc.plugins.trisurvival.crafting.CraftingIngredient
import net.trilleo.mc.plugins.trisurvival.crafting.CustomRecipe
import net.trilleo.mc.plugins.trisurvival.enchants.EnchantBook
import net.trilleo.mc.plugins.trisurvival.enchants.stats.Sharpness
import org.bukkit.Material

object SharpnessBookRecipe : CustomRecipe("sharpness_book") {

    override val shape: Array<CraftingIngredient?> = arrayOf(
        CraftingIngredient.VanillaMaterial(Material.LAPIS_LAZULI),
        CraftingIngredient.VanillaMaterial(Material.IRON_INGOT),
        CraftingIngredient.VanillaMaterial(Material.LAPIS_LAZULI),

        CraftingIngredient.VanillaMaterial(Material.IRON_INGOT),
        CraftingIngredient.VanillaMaterial(Material.BOOK),
        CraftingIngredient.VanillaMaterial(Material.IRON_INGOT),

        CraftingIngredient.VanillaMaterial(Material.LAPIS_LAZULI),
        CraftingIngredient.VanillaMaterial(Material.IRON_INGOT),
        CraftingIngredient.VanillaMaterial(Material.LAPIS_LAZULI)
    )

    override val result = EnchantBook.create(Sharpness, 1)
}
