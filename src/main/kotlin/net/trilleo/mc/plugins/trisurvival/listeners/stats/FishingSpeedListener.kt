package net.trilleo.mc.plugins.trisurvival.listeners.stats

import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

class FishingSpeedListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onFish(event: PlayerFishEvent) {
        if (event.state != PlayerFishEvent.State.FISHING) return
        val fishingSpeed = StatManager.getStat(event.player, Stat.FISHING_SPEED)
        if (fishingSpeed <= 0) return

        val hook = event.hook
        val reduction = (fishingSpeed / 100.0).coerceAtMost(0.75)
        val newWait = ((hook.maxWaitTime) * (1.0 - reduction)).toInt().coerceAtLeast(20)
        val newMinWait = ((hook.minWaitTime) * (1.0 - reduction)).toInt().coerceAtLeast(5)
        hook.maxWaitTime = newWait
        hook.minWaitTime = newMinWait
    }
}
