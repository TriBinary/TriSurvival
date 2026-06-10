package net.trilleo.mc.plugins.trisurvival.listeners.stats

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.plugin.java.JavaPlugin

class GearChangeListener(private val plugin: JavaPlugin) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onArmorChange(event: PlayerArmorChangeEvent) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            StatManager.recalculate(event.player)
        })
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onHeldChange(event: PlayerItemHeldEvent) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            StatManager.recalculate(event.player)
        })
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onSwapHands(event: PlayerSwapHandItemsEvent) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            StatManager.recalculate(event.player)
        })
    }
}
