package net.trilleo.mc.plugins.trisurvival.listeners.stats

import net.trilleo.mc.plugins.trisurvival.listeners.skills.BlockPlaceTracker
import net.trilleo.mc.plugins.trisurvival.stats.FortuneUtil
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import java.util.concurrent.ConcurrentHashMap

class MiningSpreadListener : Listener {

    companion object {
        val spreadingLocations: MutableSet<Location> = ConcurrentHashMap.newKeySet()
    }

    private val faces = arrayOf(
        BlockFace.UP, BlockFace.DOWN,
        BlockFace.NORTH, BlockFace.SOUTH,
        BlockFace.EAST, BlockFace.WEST
    )

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBreak(event: BlockBreakEvent) {
        if (event.block.location in spreadingLocations) return
        if (BlockPlaceTracker.isPlayerPlaced(event.block)) return

        val spread = StatManager.getStat(event.player, Stat.MINING_SPREAD)
        val extraBlocks = FortuneUtil.rollFortune(spread)
        if (extraBlocks <= 0) return

        val adjacent = getAdjacentMineable(event.block)
            .shuffled()
            .take(extraBlocks)

        val tool = event.player.inventory.itemInMainHand
        for (block in adjacent) {
            spreadingLocations.add(block.location)
            block.breakNaturally(tool)
            spreadingLocations.remove(block.location)
        }
    }

    private fun getAdjacentMineable(center: Block): List<Block> {
        return faces.mapNotNull { face ->
            val adj = center.getRelative(face)
            if (!adj.type.isAir && adj.type.hardness >= 0 && !BlockPlaceTracker.isPlayerPlaced(adj)) {
                adj
            } else null
        }
    }
}
