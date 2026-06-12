package net.trilleo.mc.plugins.trisurvival.listeners.items

import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.utils.sendPrefixed
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

/**
 * Custom items render as a vanilla [org.bukkit.Material] icon. When that material is a placeable
 * block, placing it would strip the custom PDC and leave a plain vanilla block — so we forbid
 * placing any custom item outright, freeing icons to use block materials safely.
 *
 * Runs before [net.trilleo.mc.plugins.trisurvival.listeners.skills.BlockPlaceTracker] (MONITOR), so a
 * cancelled placement is never flagged as player-placed. [BlockPlaceEvent] also covers its subclass
 * `BlockMultiPlaceEvent` (beds, doors, slabs).
 */
class CustomItemPlacementListener : Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (!PluginItem.isCustom(event.itemInHand)) return
        event.isCancelled = true
        event.player.sendPrefixed("<red>This item cannot be placed.")
    }
}
