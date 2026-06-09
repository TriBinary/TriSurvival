package net.trilleo.mc.plugins.trisurvival.listeners.skills

import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class BlockPlaceTracker(private val plugin: JavaPlugin) : Listener {

    private val key = NamespacedKey(plugin, PLACED_KEY)

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val chunk = event.block.chunk
        chunk.persistentDataContainer.set(
            NamespacedKey(plugin, locationKey(event.block)),
            PersistentDataType.BYTE,
            1.toByte()
        )
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
