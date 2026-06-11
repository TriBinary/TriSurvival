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
        val result = event.inventory.result ?: return
        if (VanillaItemConverter.convert(result)) {
            event.inventory.result = result
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onInventoryOpen(event: InventoryOpenEvent) {
        val player = event.player as? Player ?: return
        VanillaItemConverter.convertInventory(player.inventory)
    }
}
