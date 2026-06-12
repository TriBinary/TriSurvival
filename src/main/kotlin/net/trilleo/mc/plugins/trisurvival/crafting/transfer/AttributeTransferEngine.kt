package net.trilleo.mc.plugins.trisurvival.crafting.transfer

import net.trilleo.mc.plugins.trisurvival.registration.ItemRegistrar
import org.bukkit.inventory.ItemStack

/**
 * Applies a [RecipeTransfer] by locating the source input within a crafting grid
 * and running every [TransferRule] against the result. Reads from the grid only;
 * the inputs are never mutated.
 */
object AttributeTransferEngine {
    fun apply(transfer: RecipeTransfer, grid: Array<ItemStack?>, target: ItemStack) {
        val sourceItem = ItemRegistrar.get(transfer.sourceItemId) ?: return
        val source = grid.filterNotNull().firstOrNull { sourceItem.matches(it) } ?: return
        transfer.rules.forEach { it.apply(source, target) }
    }
}
