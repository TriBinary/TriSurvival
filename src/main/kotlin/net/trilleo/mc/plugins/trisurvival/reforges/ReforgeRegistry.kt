package net.trilleo.mc.plugins.trisurvival.reforges

import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.registration.PackageScanner
import org.bukkit.plugin.java.JavaPlugin

/**
 * Discovers every [Reforge] (including Kotlin `object` singletons) under the
 * `reforges` package and stores them keyed by [Reforge.id]. Mirrors
 * [net.trilleo.mc.plugins.trisurvival.enchants.EnchantRegistry].
 */
object ReforgeRegistry {

    private const val REFORGES_PACKAGE = "net.trilleo.mc.plugins.trisurvival.reforges"

    private val reforges = mutableMapOf<String, Reforge>()

    fun init(plugin: JavaPlugin) {
        reforges.clear()

        val classes = PackageScanner.findClasses(plugin, REFORGES_PACKAGE, Reforge::class.java)
        for (clazz in classes) {
            try {
                val reforge = resolveInstance(clazz, plugin)
                if (reforges.containsKey(reforge.id)) {
                    plugin.logger.warning(
                        "Duplicate reforge ID '${reforge.id}' — skipping ${clazz.simpleName}"
                    )
                    continue
                }
                reforges[reforge.id] = reforge
                plugin.logger.info("Registered reforge: ${reforge.id} (${clazz.simpleName})")
            } catch (e: Exception) {
                plugin.logger.severe("Failed to register reforge ${clazz.simpleName}: ${e.message}")
            }
        }

        plugin.logger.info("Registered ${reforges.size} reforge(s)")
    }

    fun get(id: String): Reforge? = reforges[id]

    fun all(): Collection<Reforge> = reforges.values.toList()

    /** Every registered reforge that may be applied to [type], sorted by name. */
    fun applicableTo(type: ItemType): List<Reforge> =
        reforges.values.filter { it.appliesTo(type) }.sortedBy { it.displayName }

    private fun resolveInstance(clazz: Class<out Reforge>, plugin: JavaPlugin): Reforge {
        try {
            val field = clazz.getDeclaredField("INSTANCE")
            if (field.trySetAccessible()) {
                return field.get(null) as Reforge
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
                "${clazz.simpleName} must be a Kotlin object, or declare a no-arg or JavaPlugin constructor"
            )
        }
    }
}
