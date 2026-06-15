package net.trilleo.mc.plugins.trisurvival.mobs.runtime

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.events.CustomMobSpawnEvent
import net.trilleo.mc.plugins.trisurvival.hologram.DamageIndicator
import net.trilleo.mc.plugins.trisurvival.hologram.Hologram
import net.trilleo.mc.plugins.trisurvival.mobs.CustomMob
import net.trilleo.mc.plugins.trisurvival.registration.MobRegistrar
import net.trilleo.mc.plugins.trisurvival.utils.PDCUtil
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Owns every live [MobInstance] and is the single entry point for spawning, damaging, and despawning
 * custom mobs. Mirrors the [net.trilleo.mc.plugins.trisurvival.stats.StatManager] shape: a
 * [ConcurrentHashMap] keyed by entity UUID, mutated on the main thread.
 */
object MobManager {

    /** Maximum tracked custom mobs allowed per chunk; change here to tune world density. */
    const val MAX_CUSTOM_MOBS_PER_CHUNK = 10

    /** Vanilla max-health the entity reports, purely so the red health-bar overlay tracks the hologram. */
    private const val VANILLA_HEALTH_DISPLAY = 40.0

    private val instances = ConcurrentHashMap<UUID, MobInstance>()
    private val mm = MiniMessage.miniMessage()
    private lateinit var plugin: JavaPlugin

    fun init(plugin: JavaPlugin) {
        this.plugin = plugin
        plugin.logger.info("MobManager initialised")
    }

    fun instanceOf(entity: Entity): MobInstance? = instances[entity.uniqueId]

    fun all(): Collection<MobInstance> = instances.values

    fun spawn(id: String, location: Location): LivingEntity? {
        val def = MobRegistrar.get(id) ?: return null
        return spawn(def, location)
    }

    fun spawn(def: CustomMob, location: Location): LivingEntity? {
        val world = location.world ?: return null
        if (countInChunk(location.chunk) >= MAX_CUSTOM_MOBS_PER_CHUNK) return null

        val event = CustomMobSpawnEvent(def, location)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) return null

        val spawned = world.spawnEntity(location, def.entityType, CreatureSpawnEvent.SpawnReason.CUSTOM)
        val entity = spawned as? LivingEntity ?: run {
            spawned.remove()
            return null
        }

        PDCUtil.set(entity, CustomMob.MOB_ID_KEY, PersistentDataType.STRING, def.id)
        entity.customName(null)
        entity.isCustomNameVisible = false
        // Tracked only in memory: don't save to disk (avoids untracked custom mobs after a chunk reload),
        // but don't let it wander-despawn while the chunk is loaded and the player is fighting it.
        entity.isPersistent = false
        (entity as? Mob)?.setRemoveWhenFarAway(false)
        entity.getAttribute(Attribute.MAX_HEALTH)?.baseValue = VANILLA_HEALTH_DISPLAY
        entity.health = VANILLA_HEALTH_DISPLAY

        val instance = MobInstance(def, entity)
        instances[entity.uniqueId] = instance

        def.customize(entity)
        instance.hologram = Hologram.spawnAbove(entity, healthLine(instance))
        def.abilities.forEach { it.onSpawn(instance) }

        return entity
    }

    /** Applies [amount] custom damage to [instance] from [source], handling death, holograms and FX. */
    fun damage(instance: MobInstance, amount: Double, source: Entity?, isCrit: Boolean = false) {
        if (source is Player) instance.lastDamager = source

        instance.currentHealth = (instance.currentHealth - amount).coerceAtLeast(0.0)
        instance.def.abilities.forEach { it.onDamaged(instance, source, amount) }
        DamageIndicator.spawn(instance.entity, amount, isCrit)

        if (instance.currentHealth <= 0.0) {
            // Triggers EntityDeathEvent; MobDeathListener handles drops, XP and cleanup.
            instance.entity.health = 0.0
            return
        }

        syncVanillaHealth(instance)
        instance.hologram?.update(healthLine(instance))
    }

    /** Refreshes hologram text and ability ticks; prunes instances whose entity is gone. Called by task. */
    fun tick() {
        val dead = mutableListOf<MobInstance>()
        for (instance in instances.values) {
            if (!instance.entity.isValid || instance.entity.isDead) {
                dead.add(instance)
                continue
            }
            instance.hologram?.update(healthLine(instance))
            instance.def.abilities.forEach { it.onTick(instance) }
        }
        dead.forEach { untrack(it) }
    }

    /** Removes the hologram and stops tracking [instance]; does not kill the entity. */
    fun untrack(instance: MobInstance) {
        instance.hologram?.remove()
        instances.remove(instance.entity.uniqueId)
    }

    /** Despawns every tracked mob and its hologram — used on plugin disable so nothing leaks. */
    fun cleanup() {
        for (instance in instances.values) {
            instance.hologram?.remove()
            if (!instance.entity.isDead) instance.entity.remove()
        }
        instances.clear()
    }

    private fun syncVanillaHealth(instance: MobInstance) {
        val max = instance.entity.getAttribute(Attribute.MAX_HEALTH)?.value ?: VANILLA_HEALTH_DISPLAY
        instance.entity.health = (instance.healthFraction * max).coerceIn(0.0, max)
    }

    private fun countInChunk(chunk: Chunk): Int =
        instances.values.count {
            val loc = it.entity.location
            it.entity.world == chunk.world && loc.blockX shr 4 == chunk.x && loc.blockZ shr 4 == chunk.z
        }

    private fun healthLine(instance: MobInstance): Component {
        val cur = instance.currentHealth.toInt()
        val max = instance.maxHealth.toInt()
        val color = instance.def.rarity.color
        return mm.deserialize(
            "$color${instance.def.displayName} <gray>$cur<dark_gray>/<gray>$max<red>❤"
        )
    }
}
