package net.trilleo.mc.plugins.trisurvival.listeners.collections

import net.trilleo.mc.plugins.trisurvival.collections.CollectionCategory
import net.trilleo.mc.plugins.trisurvival.collections.CollectionManager
import net.trilleo.mc.plugins.trisurvival.collections.CollectionRegistry
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

/** Increments Fishing collections from the item a player reels in. */
class FishingCollectionListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onFish(event: PlayerFishEvent) {
        if (event.state != PlayerFishEvent.State.CAUGHT_FISH) return
        val caught = event.caught as? Item ?: return
        val stack = caught.itemStack

        val collection = CollectionRegistry.byMaterial(stack.type) ?: return
        if (collection.category != CollectionCategory.FISHING) return

        CollectionManager.increment(event.player, collection, stack.amount.toLong())
    }
}
