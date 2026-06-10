package net.trilleo.mc.plugins.trisurvival.listeners.stats

import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityRegainHealthEvent

class VitalityListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onHeal(event: EntityRegainHealthEvent) {
        val player = event.entity as? Player ?: return
        val vitality = StatManager.getStat(player, Stat.VITALITY)
        if (vitality > 0) {
            event.amount *= (1 + vitality / 100.0)
        }
    }
}
