package net.trilleo.mc.plugins.trisurvival.listeners.stats

import net.trilleo.mc.plugins.trisurvival.listeners.skills.BlockPlaceTracker
import net.trilleo.mc.plugins.trisurvival.stats.FortuneUtil
import net.trilleo.mc.plugins.trisurvival.stats.OreTypes
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent

class MiningFortuneListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onDrop(event: BlockDropItemEvent) {
        if (event.blockState.type !in OreTypes.all) return
        if (BlockPlaceTracker.isPlayerPlaced(event.block)) return

        val fortune = StatManager.getStat(event.player, Stat.MINING_FORTUNE)
        FortuneUtil.dropExtra(event.block, event.items.map { it.itemStack }, fortune)
    }
}
