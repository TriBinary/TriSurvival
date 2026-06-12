package net.trilleo.mc.plugins.trisurvival.listeners.collections

import net.trilleo.mc.plugins.trisurvival.collections.Collection
import net.trilleo.mc.plugins.trisurvival.collections.CollectionCategory
import net.trilleo.mc.plugins.trisurvival.collections.CollectionManager
import net.trilleo.mc.plugins.trisurvival.collections.CollectionRegistry
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

/**
 * Increments Combat collections from the items a player-killed mob drops (Rotten Flesh, Bone, etc.),
 * honouring Looting since it reads the post-roll drop list.
 */
class CombatCollectionListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDeath(event: EntityDeathEvent) {
        val player = event.entity.killer ?: return

        val increments = HashMap<Collection, Long>()
        for (drop in event.drops) {
            val collection = CollectionRegistry.byMaterial(drop.type) ?: continue
            if (collection.category != CollectionCategory.COMBAT) continue
            increments[collection] = (increments[collection] ?: 0L) + drop.amount
        }

        for ((collection, amount) in increments) {
            CollectionManager.increment(player, collection, amount)
        }
    }
}
