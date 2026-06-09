package net.trilleo.mc.plugins.trisurvival.listeners.stats

import net.trilleo.mc.plugins.trisurvival.stats.DamageFormula
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class DamageListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onDamage(event: EntityDamageByEntityEvent) {
        val attacker = event.damager as? Player
        val victim = event.entity

        if (attacker != null) {
            val profile = StatManager.getProfile(attacker)
            val result = DamageFormula.calculateDamage(
                baseDamage = event.damage,
                strength = profile.strength,
                critChance = profile.critChance,
                critDamage = profile.critDamage
            )
            event.damage = result.damage

            if (result.isCrit) {
                victim.world.spawnParticle(
                    Particle.CRIT, victim.location.add(0.0, 1.0, 0.0),
                    10, 0.3, 0.3, 0.3, 0.1
                )
            }
        }

        val playerVictim = victim as? Player
        if (playerVictim != null) {
            val profile = StatManager.getProfile(playerVictim)
            event.damage = DamageFormula.reduceDamage(event.damage, profile.defense)
        }
    }
}
