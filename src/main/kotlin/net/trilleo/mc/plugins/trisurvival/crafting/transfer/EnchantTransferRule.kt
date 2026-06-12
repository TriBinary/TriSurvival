package net.trilleo.mc.plugins.trisurvival.crafting.transfer

import net.trilleo.mc.plugins.trisurvival.enchants.EnchantData
import org.bukkit.inventory.ItemStack

/** Copies all TriSurvival custom enchants from the source item onto the target. */
object EnchantTransferRule : TransferRule {
    override fun apply(source: ItemStack, target: ItemStack) {
        for ((enchant, level) in EnchantData.read(source)) {
            EnchantData.set(target, enchant, level)
        }
    }
}
