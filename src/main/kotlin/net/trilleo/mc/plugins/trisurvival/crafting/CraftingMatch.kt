package net.trilleo.mc.plugins.trisurvival.crafting

import org.bukkit.inventory.ItemStack

data class CraftingMatch(
    val result: ItemStack,
    val consumeAmounts: IntArray
) {
    init {
        require(consumeAmounts.size == 9) { "consumeAmounts must have exactly 9 entries" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CraftingMatch) return false
        return result == other.result && consumeAmounts.contentEquals(other.consumeAmounts)
    }

    override fun hashCode(): Int = 31 * result.hashCode() + consumeAmounts.contentHashCode()
}
