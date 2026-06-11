package net.trilleo.mc.plugins.trisurvival.stats

import net.trilleo.mc.plugins.trisurvival.ores.CustomOres
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

/**
 * Decides whether a held tool is the *ideal* type for a block, and how much mining speed it contributes.
 *
 * Note: Bukkit's [Block.isPreferredTool] is unsuitable here — it returns `true` for any tool on blocks
 * that don't strictly require one (sand, dirt, gravel), so a pickaxe would "qualify" on sand. Instead we
 * match the block's ideal [MiningToolType] against the tool's category. Custom ores declare their own
 * ideal tool ([net.trilleo.mc.plugins.trisurvival.ores.CustomOre.idealTool]), overriding the vanilla tag
 * of their representing block (e.g. mithril is wool but mines as a pickaxe ore).
 */
object ToolSpeed {

    /** The tool category a block is ideally broken with, or `null` if no specific tool applies. */
    private fun blockCategory(block: Block): MiningToolType? =
        CustomOres.oreAt(block)?.idealTool ?: MiningToolType.ofBlock(block.type)

    /**
     * Whether [tool] is the right tool category to earn mining-speed bonuses on [block]. Blocks with no
     * ideal tool (glass, wool, …) accept any tool, since there is no "wrong" choice to penalise.
     */
    fun isCorrectTool(tool: ItemStack, block: Block): Boolean {
        val needed = blockCategory(block) ?: return true
        return MiningToolType.of(tool) == needed
    }

    /**
     * Mining-speed contribution from a held vanilla tool, added on top of the MINING_SPEED stat. Only the
     * block's ideal tool category counts (a pickaxe does nothing on sand), and the values are tuned so a
     * correct vanilla tool reproduces roughly its vanilla break speed against the base block table.
     */
    fun contribution(tool: ItemStack, block: Block): Double {
        if (!isCorrectTool(tool, block)) return 0.0
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
