package net.trilleo.mc.plugins.trisurvival.crafting

import net.trilleo.mc.plugins.trisurvival.registration.ItemRegistrar
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

sealed interface CraftingIngredient {
    val amount: Int

    fun matches(stack: ItemStack): Boolean

    data class VanillaMaterial(
        val material: Material,
        override val amount: Int = 1
    ) : CraftingIngredient {
        override fun matches(stack: ItemStack): Boolean = stack.type == material
    }

    data class CustomItem(
        val itemId: String,
        override val amount: Int = 1
    ) : CraftingIngredient {
        override fun matches(stack: ItemStack): Boolean =
            ItemRegistrar.get(itemId)?.matches(stack) == true
    }

    data class ChoiceGroup(
        val choices: List<Material>,
        override val amount: Int = 1
    ) : CraftingIngredient {
        override fun matches(stack: ItemStack): Boolean = stack.type in choices
    }
}
