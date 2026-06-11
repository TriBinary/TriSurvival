package net.trilleo.mc.plugins.trisurvival.ores

import net.trilleo.mc.plugins.trisurvival.registration.PackageScanner
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

/**
 * Discovers every [CustomOre] subclass inside the `ores` package and registers it, keyed by
 * [CustomOre.id]. Mirrors the scan/resolve pattern of the item and recipe registrars.
 */
object CustomOreRegistry {

    private const val ORES_PACKAGE = "net.trilleo.mc.plugins.trisurvival.ores"

    private val ores = mutableMapOf<String, CustomOre>()

    fun init(plugin: JavaPlugin) {
        ores.clear()

        val oreClasses = PackageScanner.findClasses(plugin, ORES_PACKAGE, CustomOre::class.java)
        for (clazz in oreClasses) {
            try {
                val ore = resolveInstance(clazz, plugin)
                if (ores.containsKey(ore.id)) {
                    plugin.logger.warning("Duplicate custom ore ID '${ore.id}' — skipping ${clazz.simpleName}")
                    continue
                }
                ores[ore.id] = ore
                plugin.logger.info("Registered custom ore: ${ore.id} (${clazz.simpleName})")
            } catch (e: Exception) {
                plugin.logger.severe("Failed to register custom ore ${clazz.simpleName}: ${e.message}")
            }
        }

        plugin.logger.info("Registered ${ores.size} custom ore(s)")
    }

    fun get(id: String): CustomOre? = ores[id]

    fun getAll(): Collection<CustomOre> = ores.values.toList()

    fun byBlock(material: Material): List<CustomOre> = ores.values.filter { it.representingBlock == material }

    private fun resolveInstance(clazz: Class<out CustomOre>, plugin: JavaPlugin): CustomOre {
        try {
            val field = clazz.getDeclaredField("INSTANCE")
            if (field.trySetAccessible()) {
                return field.get(null) as CustomOre
            }
        } catch (_: NoSuchFieldException) {
        }

        return try {
            clazz.getDeclaredConstructor(JavaPlugin::class.java).newInstance(plugin)
        } catch (_: NoSuchMethodException) {
            try {
                clazz.getDeclaredConstructor().newInstance()
            } catch (_: NoSuchMethodException) {
                throw IllegalArgumentException(
                    "${clazz.simpleName} must be a Kotlin object, or declare either a no-arg constructor " +
                            "or a constructor accepting a single JavaPlugin parameter"
                )
            }
        }
    }
}
