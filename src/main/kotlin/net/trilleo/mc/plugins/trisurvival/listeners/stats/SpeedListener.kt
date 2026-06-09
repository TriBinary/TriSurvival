package net.trilleo.mc.plugins.trisurvival.listeners.stats

import net.trilleo.mc.plugins.trisurvival.events.StatRecalcEvent
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class SpeedListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        syncSpeed(event.player)
    }

    @EventHandler
    fun onStatRecalc(event: StatRecalcEvent) {
        syncSpeed(event.player)
    }

    private fun syncSpeed(player: Player) {
        val profile = StatManager.getProfile(player)
        // 100 speed = 0.2 vanilla (default), scale linearly
        player.walkSpeed = (profile.speed / 500.0).coerceIn(0.0, 1.0).toFloat()
    }
}
