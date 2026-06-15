package net.trilleo.mc.plugins.trisurvival.mobs.spawn

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.trilleo.mc.plugins.trisurvival.data.ServerDataManager
import net.trilleo.mc.plugins.trisurvival.mobs.CustomMob
import net.trilleo.mc.plugins.trisurvival.registration.MobRegistrar
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.plugin.java.JavaPlugin
import kotlin.random.Random

/**
 * Holds the two spawn-driving structures: in-memory [MobSpawnRule]s for vanilla-spawn replacement
 * (registered in code) and persisted [MobZone]s for periodic zone spawning (editable at runtime).
 */
object MobSpawnRegistry {

    private const val ZONES_KEY = "mob_zones"

    private val rules = mutableListOf<MobSpawnRule>()
    private val zones = mutableListOf<MobZone>()

    fun init(plugin: JavaPlugin) {
        rules.clear()
        zones.clear()
        loadZones()
        registerDefaults()
        plugin.logger.info("MobSpawnRegistry initialised (${rules.size} rule(s), ${zones.size} zone(s))")
    }

    fun registerRule(rule: MobSpawnRule) {
        rules.add(rule)
    }

    /** Returns a custom mob to replace a natural spawn of [type] at [location], or `null` for none. */
    fun match(location: Location, type: EntityType): CustomMob? {
        val worldName = location.world?.name ?: return null
        for (rule in rules) {
            if (rule.worlds.isNotEmpty() && worldName !in rule.worlds) continue
            if (rule.replaces.isNotEmpty() && type !in rule.replaces) continue
            if (Random.nextDouble() >= rule.chance) continue
            val pool = rule.mobIds.mapNotNull { MobRegistrar.get(it) }
            MobRegistrar.roll(pool)?.let { return it }
        }
        return null
    }

    fun zones(): List<MobZone> = zones.toList()

    fun addZone(zone: MobZone) {
        zones.removeAll { it.name.equals(zone.name, ignoreCase = true) }
        zones.add(zone)
        persistZones()
    }

    fun removeZone(name: String): Boolean {
        val removed = zones.removeAll { it.name.equals(name, ignoreCase = true) }
        if (removed) persistZones()
        return removed
    }

    // Demonstration rule: 15% of natural overworld zombies become Crypt Ghouls (if that mob exists).
    private fun registerDefaults() {
        registerRule(
            MobSpawnRule(
                replaces = setOf(EntityType.ZOMBIE),
                mobIds = listOf("crypt_ghoul"),
                chance = 0.15
            )
        )
    }

    private fun loadZones() {
        val array = ServerDataManager.get().getJsonArray(ZONES_KEY)
        for (element in array) {
            val obj = element.asJsonObject
            val ids = obj.getAsJsonArray("mobIds").map { it.asString }
            zones.add(
                MobZone(
                    name = obj.get("name").asString,
                    world = obj.get("world").asString,
                    x = obj.get("x").asDouble,
                    y = obj.get("y").asDouble,
                    z = obj.get("z").asDouble,
                    radius = obj.get("radius").asDouble,
                    cap = obj.get("cap").asInt,
                    mobIds = ids
                )
            )
        }
    }

    private fun persistZones() {
        val array = JsonArray()
        for (zone in zones) {
            val obj = JsonObject()
            obj.addProperty("name", zone.name)
            obj.addProperty("world", zone.world)
            obj.addProperty("x", zone.x)
            obj.addProperty("y", zone.y)
            obj.addProperty("z", zone.z)
            obj.addProperty("radius", zone.radius)
            obj.addProperty("cap", zone.cap)
            val ids = JsonArray()
            zone.mobIds.forEach { ids.add(it) }
            obj.add("mobIds", ids)
            array.add(obj)
        }
        ServerDataManager.get().set(ZONES_KEY, array)
    }
}
