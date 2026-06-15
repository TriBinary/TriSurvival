package net.trilleo.mc.plugins.trisurvival.tasks

import net.trilleo.mc.plugins.trisurvival.mobs.runtime.MobManager
import net.trilleo.mc.plugins.trisurvival.mobs.spawn.MobZone
import net.trilleo.mc.plugins.trisurvival.registration.MobRegistrar
import net.trilleo.mc.plugins.trisurvival.registration.PluginTask
import org.bukkit.Bukkit
import org.bukkit.Location
import kotlin.random.Random

/** Periodically tops up each configured [MobZone] with weighted custom mobs, up to the zone cap. */
class MobZoneSpawnTask : PluginTask(delay = 100L, period = 60L) {

    override fun run() {
        for (zone in net.trilleo.mc.plugins.trisurvival.mobs.spawn.MobSpawnRegistry.zones()) {
            val world = Bukkit.getWorld(zone.world) ?: continue
            val center = Location(world, zone.x, zone.y, zone.z)

            // Only spawn while players are nearby, so we don't churn mobs in empty chunks.
            val hasNearbyPlayer = world.players.any { it.location.distanceSquared(center) <= (zone.radius + 48) * (zone.radius + 48) }
            if (!hasNearbyPlayer) continue

            val alive = MobManager.all().count {
                it.entity.world == world && it.entity.location.distanceSquared(center) <= zone.radius * zone.radius
            }
            if (alive >= zone.cap) continue

            val pool = zone.mobIds.mapNotNull { MobRegistrar.get(it) }
            val def = MobRegistrar.roll(pool) ?: continue

            val angle = Random.nextDouble(0.0, Math.PI * 2)
            val dist = Random.nextDouble(0.0, zone.radius)
            val x = zone.x + Math.cos(angle) * dist
            val z = zone.z + Math.sin(angle) * dist
            val y = world.getHighestBlockYAt(x.toInt(), z.toInt()) + 1.0
            MobManager.spawn(def, Location(world, x, y, z))
        }
    }
}
