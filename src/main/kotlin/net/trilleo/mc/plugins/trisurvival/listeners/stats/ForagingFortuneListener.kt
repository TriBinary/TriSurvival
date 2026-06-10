package net.trilleo.mc.plugins.trisurvival.listeners.stats

import net.trilleo.mc.plugins.trisurvival.stats.FortuneUtil
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.Tag
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent

class ForagingFortuneListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onDrop(event: BlockDropItemEvent) {
        if (!Tag.LOGS.isTagged(event.blockState.type)) return

        val fortune = StatManager.getStat(event.player, Stat.FORAGING_FORTUNE)
        val extra = FortuneUtil.rollFortune(fortune)
        if (extra <= 0) return

        for (item in event.items.toList()) {
            repeat(extra) {
                event.block.world.dropItemNaturally(event.block.location, item.itemStack.clone())
            }
        }
    }
}
