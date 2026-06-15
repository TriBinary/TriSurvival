package net.trilleo.mc.plugins.trisurvival.mobs

import net.trilleo.mc.plugins.trisurvival.mobs.runtime.MobInstance
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

/**
 * A special behaviour attached to a [CustomMob]. Every hook is a no-op by default, so a mini-boss or
 * boss only overrides the moments it cares about. Abilities are plain objects referenced from a mob's
 * [CustomMob.abilities] list — there is no registry to touch.
 */
interface MobAbility {

    /** Fired once, right after the live entity is spawned and tracked. */
    fun onSpawn(instance: MobInstance) {}

    /** Fired on the mob-hologram tick cadence (every few ticks) while the mob is alive. */
    fun onTick(instance: MobInstance) {}

    /** Fired when the mob deals damage to [target], before the hit is applied to the target. */
    fun onAttack(instance: MobInstance, target: LivingEntity) {}

    /** Fired after the mob takes [amount] custom damage from [source] (may be null for environment). */
    fun onDamaged(instance: MobInstance, source: Entity?, amount: Double) {}

    /** Fired when the mob dies; [killer] is the player credited with the kill, if any. */
    fun onDeath(instance: MobInstance, killer: Player?) {}
}
