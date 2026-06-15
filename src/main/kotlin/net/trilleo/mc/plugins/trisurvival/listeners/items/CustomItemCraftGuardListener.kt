package net.trilleo.mc.plugins.trisurvival.listeners.items

import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent

/**
 * Custom items render as a vanilla [org.bukkit.Material] icon, so without a guard a vanilla recipe
 * would happily consume them (e.g. four plank-icon items crafting a vanilla crafting table). This
 * blanks the vanilla crafting result whenever any custom item sits in the grid.
 *
 * Only the vanilla [org.bukkit.inventory.CraftingInventory] path is touched — the plugin's own
 * crafting (`crafting/` package, served by its custom GUI) never fires these events.
 */
class CustomItemCraftGuardListener : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onPrepareCraft(event: PrepareItemCraftEvent) {
        if (event.inventory.matrix.any { PluginItem.isCustom(it) }) {
            event.inventory.result = null
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onCraft(event: CraftItemEvent) {
        if (event.inventory.matrix.any { PluginItem.isCustom(it) }) {
            event.isCancelled = true
        }
    }
}
