package net.trilleo.mc.plugins.trisurvival.collections

import net.trilleo.mc.plugins.trisurvival.registration.PackageScanner
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

/**
 * Discovers every [CollectionProvider] inside the `collections` package and registers the
 * [Collection]s they supply, indexing them by id, category, and source for O(1) lookups by the
 * increment hooks. Mirrors the scan/resolve pattern of the item and ore registrars.
 */
object CollectionRegistry {

    private const val COLLECTIONS_PACKAGE = "net.trilleo.mc.plugins.trisurvival.collections"

    private val collections = LinkedHashMap<String, Collection>()
    private val byMaterial = HashMap<Material, Collection>()
    private val byItemId = HashMap<String, Collection>()

    fun init(plugin: JavaPlugin) {
        collections.clear()
        byMaterial.clear()
        byItemId.clear()

        val providerClasses = PackageScanner.findClasses(plugin, COLLECTIONS_PACKAGE, CollectionProvider::class.java)
        for (clazz in providerClasses) {
            try {
                val provider = resolveInstance(clazz, plugin)
                provider.collections().forEach { register(it, plugin) }
            } catch (e: Exception) {
                plugin.logger.severe("Failed to load collection provider ${clazz.simpleName}: ${e.message}")
            }
        }

        plugin.logger.info("Registered ${collections.size} collection(s)")
    }

    fun get(id: String): Collection? = collections[id]

    fun all(): List<Collection> = collections.values.toList()

    fun byCategory(category: CollectionCategory): List<Collection> =
        collections.values.filter { it.category == category }

    fun byMaterial(material: Material): Collection? = byMaterial[material]

    fun byItemId(itemId: String): Collection? = byItemId[itemId]

    private fun register(collection: Collection, plugin: JavaPlugin) {
        if (collections.containsKey(collection.id)) {
            plugin.logger.warning("Duplicate collection id '${collection.id}' — skipping")
            return
        }
        collections[collection.id] = collection

        for (source in collection.sources) {
            when (source) {
                is CollectionSource.Vanilla -> {
                    val existing = byMaterial[source.material]
                    if (existing != null) {
                        plugin.logger.warning(
                            "Material ${source.material} maps to multiple collections " +
                                    "('${existing.id}', '${collection.id}') — keeping '${existing.id}'"
                        )
                    } else {
                        byMaterial[source.material] = collection
                    }
                }

                is CollectionSource.Custom -> {
                    val existing = byItemId[source.itemId]
                    if (existing != null) {
                        plugin.logger.warning(
                            "Custom item '${source.itemId}' maps to multiple collections " +
                                    "('${existing.id}', '${collection.id}') — keeping '${existing.id}'"
                        )
                    } else {
                        byItemId[source.itemId] = collection
                    }
                }
            }
        }
    }

    private fun resolveInstance(clazz: Class<out CollectionProvider>, plugin: JavaPlugin): CollectionProvider {
        try {
            val field = clazz.getDeclaredField("INSTANCE")
            if (field.trySetAccessible()) {
                return field.get(null) as CollectionProvider
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
