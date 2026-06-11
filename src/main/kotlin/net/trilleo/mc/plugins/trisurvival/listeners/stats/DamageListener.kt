package net.trilleo.mc.plugins.trisurvival.listeners.stats

import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.stats.DamageFormula
import net.trilleo.mc.plugins.trisurvival.stats.GearBonusReader
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import net.trilleo.mc.plugins.trisurvival.utils.PDCUtil
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
            val heldItem = attacker.inventory.itemInMainHand
            val hasStatBonuses = PDCUtil.has(heldItem, GearBonusReader.STAT_BONUSES_KEY)
                    || PDCUtil.has(heldItem, PluginItem.ITEM_ID_KEY)

            val weaponDamage = if (hasStatBonuses) profile.damage else event.damage

            val result = DamageFormula.calculateDamage(
                weaponDamage = weaponDamage,
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
            var reduced = DamageFormula.reduceDamage(event.damage, profile.defense)
            val rawHearts = (profile.health / 5.0).coerceAtLeast(2.0)
            val cappedHearts = HealthListener.customHealthToHearts(profile.health)
            if (cappedHearts < rawHearts) {
                reduced *= cappedHearts / rawHearts
            }
            event.damage = reduced
        }
    }
}
