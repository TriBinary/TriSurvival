package net.trilleo.mc.plugins.trisurvival.listeners.skills

import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class BlockPlaceTracker(private val plugin: JavaPlugin) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        mark(event.block)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPistonExtend(event: BlockPistonExtendEvent) {
        relocate(event.blocks, event.direction)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPistonRetract(event: BlockPistonRetractEvent) {
        relocate(event.blocks, event.direction)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityExplode(event: EntityExplodeEvent) {
        event.blockList().forEach { clear(it) }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockExplode(event: BlockExplodeEvent) {
        event.blockList().forEach { clear(it) }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityChangeBlock(event: EntityChangeBlockEvent) {
        clear(event.block)
    }

    /**
     * Pistons report the blocks at their pre-move positions. Snapshot which of those carried the
     * flag before clearing every old position, then re-mark the destination each moved block slides
     * into — otherwise a block sliding onto a previously-flagged neighbour could resurrect a stale flag.
     */
    private fun relocate(blocks: List<Block>, direction: BlockFace) {
        val moved = blocks.filter { isPlayerPlaced(it) }
        blocks.forEach { clear(it) }
        moved.forEach { mark(it.getRelative(direction)) }
    }

    private fun mark(block: Block) {
        block.chunk.persistentDataContainer.set(
            NamespacedKey(plugin, locationKey(block)),
            PersistentDataType.BYTE,
            1.toByte()
        )
    }

    private fun clear(block: Block) {
        block.chunk.persistentDataContainer.remove(NamespacedKey(plugin, locationKey(block)))
    }

    companion object {
        const val PLACED_KEY = "ts_placed"

        fun isPlayerPlaced(block: Block): Boolean {
            val chunk = block.chunk
            val key = NamespacedKey.fromString("trisurvival:${locationKey(block)}") ?: return false
            return chunk.persistentDataContainer.has(key, PersistentDataType.BYTE)
        }

        private fun locationKey(block: Block): String {
            val rx = block.x and 0xF
            val rz = block.z and 0xF
            return "${PLACED_KEY}_${rx}_${block.y}_${rz}"
        }
    }
}
