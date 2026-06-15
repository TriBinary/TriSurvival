package net.trilleo.mc.plugins.trisurvival.listeners.mobs

import net.trilleo.mc.plugins.trisurvival.mobs.CustomMob
import net.trilleo.mc.plugins.trisurvival.mobs.runtime.MobManager
import net.trilleo.mc.plugins.trisurvival.mobs.spawn.MobSpawnRegistry
import net.trilleo.mc.plugins.trisurvival.vanillamobs.VanillaMobs
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason

/**
 * Converts world spawns into custom mobs. A special weighted [MobSpawnRule] wins if one matches;
 * otherwise the entity is swapped for its COMMON [VanillaMobs] port. Only world-driven spawns
 * (natural spawning, spawners, chunk generation, raids, …) are replaced — eggs, breeding, buckets and
 * admin/command spawns are left vanilla.
 */
class MobSpawnReplaceListener : Listener {

    // Block player-driven / transient spawns; everything else (NATURAL, SPAWNER, CHUNK_GEN, raids,
    // conversions, …) is replaced. A blocklist guarantees passive animals are covered whatever reason
    // their spawn reports, while leaving eggs, breeding and command spawns vanilla.
    private val nonReplaceableReasons = setOf(
        SpawnReason.CUSTOM,
        SpawnReason.SPAWNER_EGG,
        SpawnReason.COMMAND,
        SpawnReason.BUCKET,
        SpawnReason.DISPENSE_EGG,
        SpawnReason.BREEDING,
        SpawnReason.EGG
    )

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onSpawn(event: CreatureSpawnEvent) {
        if (event.spawnReason in nonReplaceableReasons) return
        if (CustomMob.isCustom(event.entity)) return

        val def = MobSpawnRegistry.match(event.location, event.entityType)
            ?: VanillaMobs.get(event.entityType)
            ?: return

        event.isCancelled = true
        MobManager.spawn(def, event.location)
    }
}
