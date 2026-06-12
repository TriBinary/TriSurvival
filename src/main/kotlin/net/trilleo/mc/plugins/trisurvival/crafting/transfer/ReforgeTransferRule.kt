package net.trilleo.mc.plugins.trisurvival.crafting.transfer

import net.trilleo.mc.plugins.trisurvival.reforges.ReforgeData
import org.bukkit.inventory.ItemStack

/** Copies the TriSurvival reforge from the source item onto the target. */
object ReforgeTransferRule : TransferRule {
    override fun apply(source: ItemStack, target: ItemStack) {
        ReforgeData.read(source)?.let { ReforgeData.set(target, it) }
    }
}
