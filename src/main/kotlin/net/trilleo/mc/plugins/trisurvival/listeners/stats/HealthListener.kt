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
        StatManager.syncVanillaHealth(event.player)
    }

    @EventHandler
    fun onStatRecalc(event: StatRecalcEvent) {
        StatManager.syncVanillaHealth(event.player)
    }

    companion object {
        const val VANILLA_MAX_HEALTH = 20.0

        fun customHealthToHearts(@Suppress("UNUSED_PARAMETER") customHealth: Double): Double =
            VANILLA_MAX_HEALTH
    }
}
