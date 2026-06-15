package net.trilleo.mc.plugins.trisurvival.mobs.abilities

import net.trilleo.mc.plugins.trisurvival.mobs.MobAbility
import net.trilleo.mc.plugins.trisurvival.mobs.runtime.MobInstance
import org.bukkit.Particle
import org.bukkit.entity.Entity
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * Example ability: once the mob drops below 30% health it enters a frenzy, gaining Speed II. Stateless
 * by design (the effect's presence is the state) so a single instance can be shared across every mob
 * of a definition.
 */
class FrenzyAbility : MobAbility {

    override fun onDamaged(instance: MobInstance, source: Entity?, amount: Double) {
        if (instance.healthFraction >= 0.3) return
        if (instance.entity.hasPotionEffect(PotionEffectType.SPEED)) return
        instance.entity.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20 * 60, 1))
        instance.entity.world.spawnParticle(
            Particle.ANGRY_VILLAGER, instance.entity.location.add(0.0, 1.5, 0.0), 12, 0.3, 0.3, 0.3, 0.0
        )
    }
}
