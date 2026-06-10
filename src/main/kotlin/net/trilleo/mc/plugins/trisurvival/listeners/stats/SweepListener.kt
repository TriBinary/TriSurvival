package net.trilleo.mc.plugins.trisurvival.listeners.stats

import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class SweepListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onDamage(event: EntityDamageByEntityEvent) {
        val attacker = event.damager as? Player ?: return
        val victim = event.entity as? LivingEntity ?: return

        val sweepValue = StatManager.getStat(attacker, Stat.SWEEP)
        if (sweepValue <= 0) return

        val sweepDamage = event.finalDamage * (sweepValue / 100.0)
        val nearbyEntities = victim.getNearbyEntities(3.0, 3.0, 3.0)

        for (entity in nearbyEntities) {
            if (entity === attacker || entity === victim) continue
            if (entity !is LivingEntity) continue
            entity.damage(sweepDamage, attacker)
        }
    }
}
