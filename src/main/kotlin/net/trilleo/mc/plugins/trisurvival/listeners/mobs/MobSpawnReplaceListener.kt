package net.trilleo.mc.plugins.trisurvival.listeners.mobs

import net.trilleo.mc.plugins.trisurvival.mobs.CustomMob
import net.trilleo.mc.plugins.trisurvival.mobs.runtime.MobManager
import net.trilleo.mc.plugins.trisurvival.mobs.spawn.MobSpawnRegistry
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent

/** Rolls natural vanilla spawns against [MobSpawnRegistry] rules and replaces them with custom mobs. */
class MobSpawnReplaceListener : Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onSpawn(event: CreatureSpawnEvent) {
        when (event.spawnReason) {
            // Ignore our own spawns and intentional/admin spawns to avoid recursion.
            CreatureSpawnEvent.SpawnReason.CUSTOM,
            CreatureSpawnEvent.SpawnReason.SPAWNER_EGG,
            CreatureSpawnEvent.SpawnReason.COMMAND -> return
            else -> {}
        }
        if (CustomMob.isCustom(event.entity)) return

        val def = MobSpawnRegistry.match(event.location, event.entityType) ?: return
        event.isCancelled = true
        MobManager.spawn(def, event.location)
    }
}
