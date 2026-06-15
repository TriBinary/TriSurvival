package net.trilleo.mc.plugins.trisurvival.mobs

import net.trilleo.mc.plugins.trisurvival.mobs.runtime.MobManager
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.utils.PDCUtil
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.persistence.PersistentDataType

/**
 * A custom mob definition — the static template for a Hypixel-style mob. Mirror the [net.trilleo.mc
 * .plugins.trisurvival.registration.PluginItem] pattern: declare a Kotlin `object` extending this
 * class inside the `mobs` package (any depth) and it is auto-registered by `MobRegistrar`.
 *
 * A definition carries custom stats (read from the shared [Stat] enum), a rarity, optional abilities
 * and drops. The live Bukkit entity is produced by [spawn] and managed by [MobManager], which tracks
 * custom health, renders the health hologram, and routes all combat through the custom damage system.
 *
 * Passive mobs (cows, sheep, …) simply omit a [Stat.DAMAGE] entry: they keep custom health, rarity,
 * hologram and drops but never deal custom damage.
 */
abstract class CustomMob(val id: String) {

    abstract val displayName: String

    /** The vanilla base entity to spawn — `ZOMBIE`, `GUARDIAN`, `COW`, etc. */
    abstract val entityType: EntityType

    open val rarity: MobRarity = MobRarity.COMMON

    /** Custom stats keyed by the shared [Stat] enum. At minimum provide [Stat.HEALTH]. */
    open val stats: Map<Stat, Double> = emptyMap()

    open val abilities: List<MobAbility> = emptyList()

    open val drops: List<MobDrop> = emptyList()

    /** Combat XP granted to the killer. */
    open val baseXp: Double = 0.0

    /** Hook for equipment, potion effects, or attribute tweaks on the freshly spawned entity. */
    open fun customize(entity: LivingEntity) {}

    /** Spawns a live instance of this mob at [location], or `null` if spawning was blocked. */
    fun spawn(location: Location): LivingEntity? = MobManager.spawn(this, location)

    companion object {
        @JvmField
        val MOB_ID_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:mob_id")!!

        /** The custom-mob ID tagged on [entity], or `null` when it is not a custom mob. */
        fun idOf(entity: Entity): String? =
            PDCUtil.get(entity, MOB_ID_KEY, PersistentDataType.STRING)

        /** `true` when [entity] is a TriSurvival custom mob (carries [MOB_ID_KEY]). */
        fun isCustom(entity: Entity?): Boolean =
            entity != null && PDCUtil.has(entity, MOB_ID_KEY)
    }
}
