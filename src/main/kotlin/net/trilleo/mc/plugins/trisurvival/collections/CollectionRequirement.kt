package net.trilleo.mc.plugins.trisurvival.collections

import org.bukkit.entity.Player

/**
 * A gate requiring a player to have reached [tier] of the collection identified by [collectionId].
 * Used by recipes (and anything else) that should unlock based on collection progress.
 */
data class CollectionRequirement(val collectionId: String, val tier: Int) {

    /** Whether [player] meets this requirement. An unknown collection is treated as met. */
    fun isMet(player: Player): Boolean {
        CollectionRegistry.get(collectionId) ?: return true
        return CollectionManager.getTier(player, collectionId) >= tier
    }

    /** Human-readable description, e.g. `"Mithril III"`. */
    fun describe(): String {
        val name = CollectionRegistry.get(collectionId)?.displayName ?: collectionId
        return "$name ${CollectionFormat.roman(tier)} Collection"
    }
}
