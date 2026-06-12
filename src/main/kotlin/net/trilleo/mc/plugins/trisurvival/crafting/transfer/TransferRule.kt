package net.trilleo.mc.plugins.trisurvival.crafting.transfer

import org.bukkit.inventory.ItemStack

/**
 * A single attribute-transfer step that copies some aspect of a [source] item
 * onto a [target] item. Rules are intentionally decoupled from crafting so any
 * future upgrade path (anvil, smithing, …) can reuse them.
 */
interface TransferRule {
    fun apply(source: ItemStack, target: ItemStack)
}
