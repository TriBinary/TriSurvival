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
        if (event.regainReason == EntityRegainHealthEvent.RegainReason.SATIATED
            || event.regainReason == EntityRegainHealthEvent.RegainReason.EATING
        ) return
        if (event.regainReason == EntityRegainHealthEvent.RegainReason.CUSTOM) return

        event.isCancelled = true

        val profile = StatManager.getProfile(player)
        val vitality = profile[Stat.VITALITY]
        val customHeal = event.amount * (vitality / 100.0)

        profile.currentHealth = (profile.currentHealth + customHeal).coerceAtMost(profile.health)
        StatManager.syncVanillaHealth(player)
    }
}
