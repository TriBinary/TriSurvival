package net.trilleo.mc.plugins.trisurvival.registration

import net.trilleo.mc.plugins.trisurvival.mobs.CustomMob
import org.bukkit.plugin.java.JavaPlugin
import kotlin.random.Random

/**
 * Discovers all [CustomMob] subclasses (including Kotlin `object` singletons) inside the `mobs`
 * package (and its subpackages) and registers them in an in-memory registry keyed by [CustomMob.id].
 *
 * Resolution order mirrors [ItemRegistrar]: Kotlin `object` `INSTANCE` field, then a [JavaPlugin]
 * constructor, then a no-arg constructor.
 */
object MobRegistrar {

    private const val MOBS_PACKAGE = "net.trilleo.mc.plugins.trisurvival.mobs"

    private val mobs = mutableMapOf<String, CustomMob>()

    fun registerAll(plugin: JavaPlugin) {
        mobs.clear()

        val mobClasses = PackageScanner.findClasses(plugin, MOBS_PACKAGE, CustomMob::class.java)

        for (mobClass in mobClasses) {
            try {
                val mob = resolveInstance(mobClass, plugin)
                if (mobs.containsKey(mob.id)) {
                    plugin.logger.warning(
                        "Duplicate custom mob ID '${mob.id}' — skipping ${mobClass.simpleName}"
                    )
                    continue
                }
                mobs[mob.id] = mob
                plugin.logger.info("Registered custom mob: ${mob.id} (${mobClass.simpleName})")
            } catch (e: Exception) {
                plugin.logger.severe("Failed to register mob ${mobClass.simpleName}: ${e.message}")
            }
        }

        plugin.logger.info("Registered ${mobs.size} custom mob(s)")
    }

    fun get(id: String): CustomMob? = mobs[id]

    fun getAll(): Collection<CustomMob> = mobs.values.toList()

    /** Weighted random pick from [pool] using [CustomMob.rarity] weights, or `null` when empty. */
    fun roll(pool: List<CustomMob>): CustomMob? {
        if (pool.isEmpty()) return null
        val totalWeight = pool.sumOf { it.rarity.weight }
        if (totalWeight <= 0) return null
        var roll = Random.nextInt(totalWeight)
        for (mob in pool) {
            roll -= mob.rarity.weight
            if (roll < 0) return mob
        }
        return pool.last()
    }

    private fun resolveInstance(clazz: Class<out CustomMob>, plugin: JavaPlugin): CustomMob {
        try {
            val field = clazz.getDeclaredField("INSTANCE")
            if (field.trySetAccessible()) {
                return field.get(null) as CustomMob
            }
        } catch (_: NoSuchFieldException) {
        }

        try {
            return clazz.getDeclaredConstructor(JavaPlugin::class.java).newInstance(plugin)
        } catch (_: NoSuchMethodException) {
        }

        return try {
            clazz.getDeclaredConstructor().newInstance()
        } catch (_: NoSuchMethodException) {
            throw IllegalArgumentException(
                "${clazz.simpleName} must be a Kotlin object, or declare either a no-arg constructor " +
                        "or a constructor accepting a single JavaPlugin parameter"
            )
        }
    }
}
