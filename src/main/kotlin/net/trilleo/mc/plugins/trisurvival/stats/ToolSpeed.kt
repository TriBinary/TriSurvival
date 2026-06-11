package net.trilleo.mc.plugins.trisurvival.stats

import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

/**
 * Mining-speed contribution from a held vanilla tool, added on top of the MINING_SPEED stat. Only the
 * block's preferred tool category counts (a pickaxe does nothing on dirt), and the values are tuned so
 * a correct vanilla tool reproduces roughly its vanilla break speed against the base block table.
 */
object ToolSpeed {

    fun contribution(tool: ItemStack, block: Block): Double {
        if (!block.isPreferredTool(tool)) return 0.0
        val name = tool.type.name
        return when {
            name.startsWith("WOODEN_") -> 20.0
            name.startsWith("GOLDEN_") -> 120.0
            name.startsWith("STONE_") -> 40.0
            name.startsWith("IRON_") -> 60.0
            name.startsWith("DIAMOND_") -> 80.0
            name.startsWith("NETHERITE_") -> 90.0
            else -> 0.0
        }
    }
}
