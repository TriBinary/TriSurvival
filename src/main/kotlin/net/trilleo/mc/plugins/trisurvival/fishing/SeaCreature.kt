package net.trilleo.mc.plugins.trisurvival.fishing

import net.trilleo.mc.plugins.trisurvival.mobs.CustomMob
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

/**
 * A sea creature is a [CustomMob] that is summoned by fishing rather than by world spawning. Concrete
 * creatures live under the `mobs` package (so they are auto-registered) and override the usual mob
 * fields plus, optionally, [onFishedUp] to react to the player who reeled them in.
 */
abstract class SeaCreature(id: String) : CustomMob(id) {

    /** Called right after the creature is spawned from a fishing hook, with the angler. */
    open fun onFishedUp(entity: LivingEntity, player: Player) {}

    /** Spawns this creature at the hook [location] and runs the [onFishedUp] hook. */
    fun spawnFromHook(location: Location, player: Player): LivingEntity? {
        val entity = spawn(location) ?: return null
        onFishedUp(entity, player)
        return entity
    }
}
