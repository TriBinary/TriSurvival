package net.trilleo.mc.plugins.trisurvival.listeners.mobs

import net.trilleo.mc.plugins.trisurvival.mobs.runtime.MobManager
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.stats.DamageFormula
import net.trilleo.mc.plugins.trisurvival.stats.GearBonusReader
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import net.trilleo.mc.plugins.trisurvival.utils.PDCUtil
import org.bukkit.Particle
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

/**
 * Routes all combat involving custom mobs through the custom stat system:
 *
 * - **mob → player** ([onMobAttack], LOW): overwrites the vanilla hit with the mob's [DAMAGE] stat so
 *   the existing player-defense handling in `DamageListener` finishes the job unchanged.
 * - **anything → mob** ([onMobDamaged], HIGH): cancels vanilla damage and applies the hit to the mob's
 *   custom health pool via [MobManager.damage]. Player hits use the same [DamageFormula] as players do.
 */
class MobDamageListener : org.bukkit.event.Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onMobAttack(event: EntityDamageByEntityEvent) {
        val mob = MobManager.instanceOf(event.damager) ?: return
        val target = event.entity as? LivingEntity ?: return
        val damage = mob.profile.damage
        if (damage > 0.0) event.damage = damage
        mob.def.abilities.forEach { it.onAttack(mob, target) }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onMobDamaged(event: EntityDamageByEntityEvent) {
        val mob = MobManager.instanceOf(event.entity) ?: return
        event.isCancelled = true

        val attacker = event.damager as? Player
        if (attacker != null) {
            val profile = StatManager.getProfile(attacker)
            val held = attacker.inventory.itemInMainHand
            val hasStatBonuses = PDCUtil.has(held, GearBonusReader.STAT_BONUSES_KEY)
                    || PDCUtil.has(held, PluginItem.ITEM_ID_KEY)
            val baseDamage = if (hasStatBonuses) profile.damage else event.damage

            val result = DamageFormula.calculateDamage(
                weaponDamage = baseDamage,
                strength = profile.strength,
                critChance = profile.critChance,
                critDamage = profile.critDamage
            )
            if (result.isCrit) {
                mob.entity.world.spawnParticle(
                    Particle.CRIT, mob.entity.location.add(0.0, 1.0, 0.0),
                    10, 0.3, 0.3, 0.3, 0.1
                )
            }
            val reduced = DamageFormula.reduceDamage(result.damage, mob.profile.defense)
            MobManager.damage(mob, reduced, attacker, result.isCrit)
        } else {
            val reduced = DamageFormula.reduceDamage(event.damage, mob.profile.defense)
            MobManager.damage(mob, reduced, event.damager, false)
        }
    }

    // Environmental damage (fall, fire, drowning, …) also routes through the custom health pool.
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEnvironmentalDamage(event: EntityDamageEvent) {
        if (event is EntityDamageByEntityEvent) return
        val mob = MobManager.instanceOf(event.entity) ?: return
        event.isCancelled = true
        MobManager.damage(mob, event.damage, null, false)
    }
}
