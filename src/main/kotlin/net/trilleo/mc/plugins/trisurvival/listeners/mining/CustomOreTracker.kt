package net.trilleo.mc.plugins.trisurvival.listeners.mining

import net.trilleo.mc.plugins.trisurvival.ores.CustomOres
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityExplodeEvent

/**
 * Keeps the custom-ore chunk-PDC tags consistent when the world moves or destroys ore blocks outside
 * of normal mining. Pistons relocate the tag with the block; explosions, entity block changes and
 * burning clear the now-stale tag so a future block at that coordinate is not misread as an ore.
 */
class CustomOreTracker : Listener {

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
        event.blockList().forEach { CustomOres.clearMark(it) }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockExplode(event: BlockExplodeEvent) {
        event.blockList().forEach { CustomOres.clearMark(it) }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityChangeBlock(event: EntityChangeBlockEvent) {
        CustomOres.clearMark(event.block)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBurn(event: BlockBurnEvent) {
        CustomOres.clearMark(event.block)
    }

    /**
     * Snapshot which moved blocks carry an ore tag before clearing every old position, then re-write
     * each tag at the block's destination — otherwise a block sliding onto a previously-tagged
     * neighbour could resurrect a stale tag.
     */
    private fun relocate(blocks: List<Block>, direction: BlockFace) {
        val moved = blocks.mapNotNull { block -> CustomOres.tagId(block)?.let { block to it } }
        blocks.forEach { CustomOres.clearMark(it) }
        moved.forEach { (block, id) -> CustomOres.writeTag(block.getRelative(direction), id) }
    }
}
