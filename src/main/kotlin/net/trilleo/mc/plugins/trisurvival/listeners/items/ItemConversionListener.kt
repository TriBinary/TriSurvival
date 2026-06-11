package net.trilleo.mc.plugins.trisurvival.listeners.items

import net.trilleo.mc.plugins.trisurvival.items.VanillaItemConverter
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class ItemConversionListener(private val plugin: JavaPlugin) : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onJoin(event: PlayerJoinEvent) {
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            if (event.player.isOnline) {
                VanillaItemConverter.convertInventory(event.player.inventory)
            }
        }, 5L)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPickup(event: EntityPickupItemEvent) {
        if (event.entity !is Player) return
        VanillaItemConverter.convert(event.item.itemStack)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onCraft(event: CraftItemEvent) {
        // Convert the result template so a normal (cursor) craft is already converted.
        event.inventory.result?.let { result ->
            if (VanillaItemConverter.convert(result)) {
                event.inventory.result = result
            }
        }

        // Shift-click crafting bulk-moves results straight into the inventory, bypassing the template
        // above, so convert the whole inventory (and cursor) once the craft settles next tick.
        val player = event.whoClicked as? Player ?: return
        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (!player.isOnline) return@Runnable
            VanillaItemConverter.convertInventory(player.inventory)
            val cursor = player.itemOnCursor
            if (VanillaItemConverter.convert(cursor)) {
                player.setItemOnCursor(cursor)
            }
        })
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onInventoryOpen(event: InventoryOpenEvent) {
        val player = event.player as? Player ?: return
        VanillaItemConverter.convertInventory(player.inventory)
    }
}
