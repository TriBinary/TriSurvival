package net.trilleo.mc.plugins.trisurvival.mobs.runtime

import net.trilleo.mc.plugins.trisurvival.hologram.Hologram
import net.trilleo.mc.plugins.trisurvival.mobs.CustomMob
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

/**
 * A live, tracked instance of a [CustomMob]: the spawned [entity] plus its custom-health state and
 * health hologram. Real combat damage is applied to [currentHealth] (which can be far larger than any
 * vanilla health pool); the vanilla health bar is only kept in sync for the red overlay.
 */
class MobInstance(
    val def: CustomMob,
    val entity: LivingEntity
) {

    val profile: MobProfile = MobProfile(def.stats)

    val maxHealth: Double = profile.health.takeIf { it > 0.0 } ?: DEFAULT_HEALTH

    var currentHealth: Double = maxHealth

    /** The last player to damage this mob, used to credit kills when vanilla damage is cancelled. */
    var lastDamager: Player? = null

    var hologram: Hologram? = null

    val healthFraction: Double get() = if (maxHealth > 0) currentHealth / maxHealth else 0.0

    companion object {
        const val DEFAULT_HEALTH = 20.0
    }
}
