package net.trilleo.mc.plugins.trisurvival.listeners.stats

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerJoinEvent

class HungerListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onFoodChange(event: FoodLevelChangeEvent) {
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onHungerRegen(event: EntityRegainHealthEvent) {
        if (event.entity !is Player) return
        if (event.regainReason == EntityRegainHealthEvent.RegainReason.SATIATED
            || event.regainReason == EntityRegainHealthEvent.RegainReason.EATING
        ) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.foodLevel = 20
        event.player.saturation = 0f
        event.player.exhaustion = 0f
    }
}
