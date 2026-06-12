package net.trilleo.mc.plugins.trisurvival.listeners.collections

import net.trilleo.mc.plugins.trisurvival.collections.Collection
import net.trilleo.mc.plugins.trisurvival.collections.CollectionCategory
import net.trilleo.mc.plugins.trisurvival.collections.CollectionManager
import net.trilleo.mc.plugins.trisurvival.collections.CollectionRegistry
import net.trilleo.mc.plugins.trisurvival.listeners.skills.BlockPlaceTracker
import net.trilleo.mc.plugins.trisurvival.ores.CustomOres
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

/**
 * Increments Mining/Farming/Foraging collections when a player breaks a source block. Counts the
 * block's actual drops, skips immature crops and player-placed blocks (for filtered collections),
 * and defers custom ores to [net.trilleo.mc.plugins.trisurvival.ores.CustomOreRewards] so their
 * representing block (e.g. an iron block) is never mis-counted.
 */
class BlockCollectionListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val block = event.block

        // Custom ores award their own collection in CustomOreRewards.
        if (CustomOres.oreAt(block) != null) return

        val data = block.blockData
        if (data is Ageable && data.age < data.maximumAge) return

        val tool = event.player.inventory.itemInMainHand
        val drops = block.getDrops(tool)
        if (drops.isEmpty()) return

        val placed by lazy { BlockPlaceTracker.isPlayerPlaced(block) }
        val increments = HashMap<Collection, Long>()
        for (drop in drops) {
            val collection = CollectionRegistry.byMaterial(drop.type) ?: continue
            if (collection.category !in BLOCK_CATEGORIES) continue
            if (collection.placedFilter && placed) continue
            increments[collection] = (increments[collection] ?: 0L) + drop.amount
        }

        for ((collection, amount) in increments) {
            CollectionManager.increment(event.player, collection, amount)
        }
    }

    companion object {
        private val BLOCK_CATEGORIES = setOf(
            CollectionCategory.MINING,
            CollectionCategory.FARMING,
            CollectionCategory.FORAGING
        )
    }
}
