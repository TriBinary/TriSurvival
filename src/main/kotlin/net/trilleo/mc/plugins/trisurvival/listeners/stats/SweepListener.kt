package net.trilleo.mc.plugins.trisurvival.listeners.stats

import net.trilleo.mc.plugins.trisurvival.listeners.skills.BlockPlaceTracker
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.Location
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class SweepListener : Listener {

    companion object {
        val sweepingLocations: MutableSet<Location> = ConcurrentHashMap.newKeySet()
    }

    private val searchOffsets: List<Triple<Int, Int, Int>> = buildList {
        for (dx in -1..1) for (dy in -1..1) for (dz in -1..1) {
            if (dx != 0 || dy != 0 || dz != 0) add(Triple(dx, dy, dz))
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBreak(event: BlockBreakEvent) {
        if (!Tag.LOGS.isTagged(event.block.type)) return
        if (event.block.location in sweepingLocations) return
        if (BlockPlaceTracker.isPlayerPlaced(event.block)) return

        val sweepCount = StatManager.getStat(event.player, Stat.SWEEP).toInt()
        if (sweepCount <= 0) return

        val connectedLogs = findConnectedLogs(event.block, sweepCount)
        val tool = event.player.inventory.itemInMainHand

        for (log in connectedLogs) {
            sweepingLocations.add(log.location)
            log.breakNaturally(tool)
            sweepingLocations.remove(log.location)
        }
    }

    private fun findConnectedLogs(origin: Block, maxCount: Int): List<Block> {
        val result = mutableListOf<Block>()
        val visited = mutableSetOf(origin.location)
        val queue = LinkedList<Block>()
        queue.add(origin)

        while (queue.isNotEmpty() && result.size < maxCount) {
            val current = queue.poll()
            for ((dx, dy, dz) in searchOffsets) {
                val neighbor = current.world.getBlockAt(
                    current.x + dx, current.y + dy, current.z + dz
                )
                if (neighbor.location in visited) continue
                visited.add(neighbor.location)
                if (!Tag.LOGS.isTagged(neighbor.type)) continue
                if (BlockPlaceTracker.isPlayerPlaced(neighbor)) continue
                result.add(neighbor)
                queue.add(neighbor)
                if (result.size >= maxCount) break
            }
        }

        return result
    }
}
