package net.trilleo.mc.plugins.trisurvival.listeners.stats

import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.stats.DamageFormula
import net.trilleo.mc.plugins.trisurvival.stats.GearBonusReader
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import net.trilleo.mc.plugins.trisurvival.utils.PDCUtil
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.util.Vector
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class DamageListener : Listener {

    companion object {
        private const val DAMAGE_COOLDOWN_TICKS = 10
        private const val KNOCKBACK_STRENGTH = 0.4
        private val lastDamageTick = ConcurrentHashMap<UUID, Long>()

        fun clearPlayer(uuid: UUID) {
            lastDamageTick.remove(uuid)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onDamage(event: EntityDamageByEntityEvent) {
        val attacker = event.damager as? Player
        val victim = event.entity

        if (attacker != null) {
            val profile = StatManager.getProfile(attacker)
            val heldItem = attacker.inventory.itemInMainHand
            val hasStatBonuses = PDCUtil.has(heldItem, GearBonusReader.STAT_BONUSES_KEY)
                    || PDCUtil.has(heldItem, PluginItem.ITEM_ID_KEY)

            var baseDamage = if (hasStatBonuses) profile.damage else event.damage

            if (!hasStatBonuses) {
                baseDamage = removeVanillaCrit(attacker, baseDamage)
            }

            val result = DamageFormula.calculateDamage(
                weaponDamage = baseDamage,
                strength = profile.strength,
                critChance = profile.critChance,
                critDamage = profile.critDamage
            )

            if (result.isCrit) {
                victim.world.spawnParticle(
                    Particle.CRIT, victim.location.add(0.0, 1.0, 0.0),
                    10, 0.3, 0.3, 0.3, 0.1
                )
            }

            val playerVictim = victim as? Player
            if (playerVictim != null) {
                event.isCancelled = true
                val victimProfile = StatManager.getProfile(playerVictim)
                val reduced = DamageFormula.reduceDamage(result.damage, victimProfile.defense)
                applyKnockback(playerVictim, event.damager)
                applyCustomDamage(playerVictim, reduced)
            } else {
                event.damage = result.damage
            }
        } else {
            val playerVictim = victim as? Player
            if (playerVictim != null) {
                event.isCancelled = true
                val victimProfile = StatManager.getProfile(playerVictim)
                val reduced = DamageFormula.reduceDamage(event.damage, victimProfile.defense)
                applyKnockback(playerVictim, event.damager)
                applyCustomDamage(playerVictim, reduced)
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onEnvironmentalDamage(event: EntityDamageEvent) {
        if (event is EntityDamageByEntityEvent) return
        val player = event.entity as? Player ?: return

        event.isCancelled = true

        val currentTick = player.world.gameTime
        val uuid = player.uniqueId
        val last = lastDamageTick[uuid] ?: 0L
        if (currentTick - last < DAMAGE_COOLDOWN_TICKS) return
        lastDamageTick[uuid] = currentTick

        applyCustomDamage(player, event.damage)
    }

    private fun applyCustomDamage(player: Player, customDamage: Double) {
        val profile = StatManager.getProfile(player)
        profile.currentHealth = (profile.currentHealth - customDamage).coerceAtLeast(0.0)
        player.playSound(player, Sound.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1.0F, 1.0F)
        StatManager.syncVanillaHealth(player)

        if (profile.currentHealth <= 0) {
            player.health = 0.0
        } else {
            player.damage(0.0)
        }
    }

    // Cancelling the damage event also cancels vanilla knockback, so reapply it manually,
    // otherwise mobs can damage-spam a stationary player with no pushback.
    private fun applyKnockback(victim: Player, source: Entity) {
        val resistance = victim.getAttribute(Attribute.KNOCKBACK_RESISTANCE)?.value ?: 0.0
        val strength = KNOCKBACK_STRENGTH * (1.0 - resistance)
        if (strength <= 0.0) return

        var dir = victim.location.toVector().subtract(source.location.toVector()).setY(0.0)
        if (dir.lengthSquared() < 1.0e-6) {
            dir = source.location.direction.setY(0.0)
        }
        if (dir.lengthSquared() < 1.0e-6) return
        dir.normalize().multiply(strength)

        val current = victim.velocity
        victim.velocity = Vector(
            current.x / 2.0 + dir.x,
            (current.y / 2.0 + strength).coerceAtMost(KNOCKBACK_STRENGTH),
            current.z / 2.0 + dir.z
        )
    }

    private fun removeVanillaCrit(attacker: Player, damage: Double): Double {
        @Suppress("DEPRECATION")
        if (attacker.fallDistance > 0f && !attacker.isOnGround
            && !attacker.isInsideVehicle && !attacker.isSprinting
        ) {
            return damage / 1.5
        }
        return damage
    }
}
