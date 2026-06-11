package net.trilleo.mc.plugins.trisurvival.listeners.enchants

import net.trilleo.mc.plugins.trisurvival.registration.GUIManager
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class GrindstoneOpenListener : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        if (event.clickedBlock?.type != Material.GRINDSTONE) return
        event.isCancelled = true
        GUIManager.open(event.player, "grindstone")
    }
}
