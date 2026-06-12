package net.trilleo.mc.plugins.trisurvival.guis.collections

import net.trilleo.mc.plugins.trisurvival.collections.CollectionCategory
import org.bukkit.NamespacedKey
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Carries the selected [CollectionCategory] across the collections menu and list GUIs, since
 * [net.trilleo.mc.plugins.trisurvival.registration.GUIManager.open] only takes a GUI id.
 */
object CollectionSession {

    /** PDC key on a clicked category icon → [CollectionCategory.name]. */
    @JvmField
    val CATEGORY_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:collection_category")!!

    private val categories = ConcurrentHashMap<UUID, CollectionCategory>()

    fun setCategory(uuid: UUID, category: CollectionCategory) {
        categories[uuid] = category
    }

    fun getCategory(uuid: UUID): CollectionCategory? = categories[uuid]

    fun clear(uuid: UUID) {
        categories.remove(uuid)
    }
}
