package net.trilleo.mc.plugins.trisurvival.listeners.stats

import net.trilleo.mc.plugins.trisurvival.events.StatRecalcEvent
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class HealthListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        syncHealth(event.player)
    }

    @EventHandler
    fun onStatRecalc(event: StatRecalcEvent) {
        syncHealth(event.player)
    }

    private fun syncHealth(player: org.bukkit.entity.Player) {
        val profile = StatManager.getProfile(player)
        val maxHearts = (profile.health / 5.0).coerceAtLeast(2.0)
        player.getAttribute(Attribute.MAX_HEALTH)?.baseValue = maxHearts
        if (player.health > maxHearts) {
            player.health = maxHearts
        }
    }
}
