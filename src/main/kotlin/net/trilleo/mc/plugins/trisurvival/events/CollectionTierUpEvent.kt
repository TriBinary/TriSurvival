package net.trilleo.mc.plugins.trisurvival.events

import net.trilleo.mc.plugins.trisurvival.collections.Collection
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Fired by `CollectionManager` after a player crosses into a higher collection tier.
 */
class CollectionTierUpEvent(
    val player: Player,
    val collection: Collection,
    val oldTier: Int,
    val newTier: Int
) : Event() {

    override fun getHandlers(): HandlerList = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
