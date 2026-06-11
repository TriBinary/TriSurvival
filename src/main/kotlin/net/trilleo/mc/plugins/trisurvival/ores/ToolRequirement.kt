package net.trilleo.mc.plugins.trisurvival.ores

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/** Pickaxe tiers ranked by mining capability (wood and gold share the lowest rank, as in vanilla). */
enum class ToolTier(val rank: Int) {
    NONE(0),
    WOOD(1),
    GOLD(1),
    STONE(2),
    IRON(3),
    DIAMOND(4),
    NETHERITE(5)
}

/**
 * Resolves the minimum-tool gate for custom ores. Vanilla blocks already enforce correct-tool drops
 * through `player.breakBlock`, so this only governs the representing blocks (e.g. wool) that vanilla
 * considers tool-free.
 */
object ToolRequirement {

    fun pickaxeTier(material: Material): ToolTier? {
        val name = material.name
        if (!name.endsWith("_PICKAXE")) return null
        return when {
            name.startsWith("WOODEN") -> ToolTier.WOOD
            name.startsWith("GOLDEN") -> ToolTier.GOLD
            name.startsWith("STONE") -> ToolTier.STONE
            name.startsWith("IRON") -> ToolTier.IRON
            name.startsWith("DIAMOND") -> ToolTier.DIAMOND
            name.startsWith("NETHERITE") -> ToolTier.NETHERITE
            else -> null
        }
    }

    /** Whether [tool] satisfies [ore]'s pickaxe-type and minimum-tier requirements. */
    fun meets(tool: ItemStack, ore: CustomOre): Boolean {
        if (!ore.requiresPickaxe && ore.minToolTier == ToolTier.NONE) return true
        val tier = pickaxeTier(tool.type)
        if (ore.requiresPickaxe && tier == null) return false
        return (tier ?: ToolTier.NONE).rank >= ore.minToolTier.rank
    }
}
