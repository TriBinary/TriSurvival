package net.trilleo.mc.plugins.trisurvival.ores

import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.persistence.PersistentDataType

/**
 * Runtime identity helpers for custom ore blocks.
 *
 * A custom ore is a plain vanilla block, so its identity cannot live on the block itself. It is stored
 * in the owning chunk's PDC keyed by the block's in-chunk coordinate — the same per-coordinate scheme
 * as [net.trilleo.mc.plugins.trisurvival.listeners.skills.BlockPlaceTracker], under a separate key
 * prefix so the two never collide.
 */
object CustomOres {

    private const val ORE_KEY = "ts_ore"

    /** Places [ore]'s representing block at [block] and tags it as this ore (no physics update). */
    fun place(block: Block, ore: CustomOre) {
        block.setType(ore.representingBlock, false)
        block.chunk.persistentDataContainer.set(key(block), PersistentDataType.STRING, ore.id)
    }

    /** Returns the custom ore recorded at [block], or `null` if the block is not a tagged ore. */
    fun oreAt(block: Block): CustomOre? {
        val id = block.chunk.persistentDataContainer.get(key(block), PersistentDataType.STRING) ?: return null
        return CustomOreRegistry.get(id)
    }

    /** Removes the ore tag at [block] (called once the block is broken). */
    fun clearMark(block: Block) {
        block.chunk.persistentDataContainer.remove(key(block))
    }

    /** Returns the raw ore id recorded at [block], or `null` if untagged. */
    fun tagId(block: Block): String? =
        block.chunk.persistentDataContainer.get(key(block), PersistentDataType.STRING)

    /** Writes an ore [id] tag at [block] without touching the block itself (used when a tag relocates). */
    fun writeTag(block: Block, id: String) {
        block.chunk.persistentDataContainer.set(key(block), PersistentDataType.STRING, id)
    }

    private fun key(block: Block): NamespacedKey {
        val rx = block.x and 0xF
        val rz = block.z and 0xF
        return NamespacedKey.fromString("trisurvival:${ORE_KEY}_${rx}_${block.y}_${rz}")!!
    }
}
