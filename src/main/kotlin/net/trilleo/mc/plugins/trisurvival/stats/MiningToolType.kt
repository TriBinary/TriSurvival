package net.trilleo.mc.plugins.trisurvival.stats

import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.utils.PDCUtil
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * The tool category a block is ideally mined with. Mining-speed bonuses (the MINING_SPEED stat, tool
 * contribution, and Mining Spread) only apply when the held tool's category matches the block's, so a
 * high-speed pickaxe can't tear through sand.
 */
enum class MiningToolType {
    PICKAXE, SHOVEL, AXE, HOE;

    companion object {
        /**
         * The category of a held [tool], or `null` if it is not a recognised mining tool. A custom item's
         * declared [ItemType] takes precedence over its material, so a Drill (prismarine shard, not a
         * vanilla pickaxe) still mines as a pickaxe.
         */
        fun of(tool: ItemStack): MiningToolType? {
            ofCustomType(tool)?.let { return it }
            val name = tool.type.name
            return when {
                name.endsWith("_PICKAXE") -> PICKAXE
                name.endsWith("_SHOVEL") -> SHOVEL
                name.endsWith("_AXE") -> AXE
                name.endsWith("_HOE") -> HOE
                else -> null
            }
        }

        private fun ofCustomType(tool: ItemStack): MiningToolType? {
            val typeName = PDCUtil.get(tool, PluginItem.ITEM_TYPE_KEY, PersistentDataType.STRING) ?: return null
            return when (typeName) {
                ItemType.DRILL.name, ItemType.PICKAXE.name -> PICKAXE
                ItemType.AXE.name -> AXE
                ItemType.HOE.name -> HOE
                else -> null
            }
        }

        /** The ideal category for a vanilla [material] from its `MINEABLE_*` tag, or `null` if none. */
        fun ofBlock(material: Material): MiningToolType? = when {
            Tag.MINEABLE_PICKAXE.isTagged(material) -> PICKAXE
            Tag.MINEABLE_SHOVEL.isTagged(material) -> SHOVEL
            Tag.MINEABLE_AXE.isTagged(material) -> AXE
            Tag.MINEABLE_HOE.isTagged(material) -> HOE
            else -> null
        }
    }
}
