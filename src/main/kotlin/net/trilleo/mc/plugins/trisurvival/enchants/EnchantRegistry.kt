package net.trilleo.mc.plugins.trisurvival.enchants

import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.registration.PackageScanner
import org.bukkit.plugin.java.JavaPlugin

/**
 * Discovers every [CustomEnchant] (including Kotlin `object` singletons) under
 * the `enchants` package and stores them keyed by [CustomEnchant.id]. Mirrors
 * [net.trilleo.mc.plugins.trisurvival.registration.ItemRegistrar].
 */
object EnchantRegistry {

    private const val ENCHANTS_PACKAGE = "net.trilleo.mc.plugins.trisurvival.enchants"

    private val enchants = mutableMapOf<String, CustomEnchant>()

    fun init(plugin: JavaPlugin) {
        enchants.clear()

        val classes = PackageScanner.findClasses(plugin, ENCHANTS_PACKAGE, CustomEnchant::class.java)
        for (clazz in classes) {
            try {
                val enchant = resolveInstance(clazz, plugin)
                if (enchants.containsKey(enchant.id)) {
                    plugin.logger.warning(
                        "Duplicate enchant ID '${enchant.id}' — skipping ${clazz.simpleName}"
                    )
                    continue
                }
                enchants[enchant.id] = enchant
                plugin.logger.info("Registered enchant: ${enchant.id} (${clazz.simpleName})")
            } catch (e: Exception) {
                plugin.logger.severe("Failed to register enchant ${clazz.simpleName}: ${e.message}")
            }
        }

        plugin.logger.info("Registered ${enchants.size} enchant(s)")
    }

    fun get(id: String): CustomEnchant? = enchants[id]

    fun all(): Collection<CustomEnchant> = enchants.values.toList()

    /** Every registered enchant that may be applied to [type], sorted by name. */
    fun applicableTo(type: ItemType): List<CustomEnchant> =
        enchants.values.filter { it.appliesTo(type) }.sortedBy { it.displayName }

    private fun resolveInstance(clazz: Class<out CustomEnchant>, plugin: JavaPlugin): CustomEnchant {
        try {
            val field = clazz.getDeclaredField("INSTANCE")
            if (field.trySetAccessible()) {
                return field.get(null) as CustomEnchant
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
