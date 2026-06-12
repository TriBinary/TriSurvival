package net.trilleo.mc.plugins.trisurvival.crafting.transfer

/**
 * Declares that a recipe should carry attributes over from one of its input
 * items (the upgrade base) onto the crafted result.
 *
 * @param sourceItemId the custom item id of the input to copy from (e.g. `"ruby_sword"`)
 * @param rules        the transfer steps to apply; defaults to custom-enchant transfer
 */
data class RecipeTransfer(
    val sourceItemId: String,
    val rules: List<TransferRule> = listOf(EnchantTransferRule)
)
