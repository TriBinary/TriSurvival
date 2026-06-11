package net.trilleo.mc.plugins.trisurvival.listeners.enchants

import net.trilleo.mc.plugins.trisurvival.registration.GUIManager
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class EnchantAnvilListener : Listener {

    private val anvils = setOf(Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL)

    @EventHandler(priority = EventPriority.HIGH)
    fun onInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        if (event.clickedBlock?.type !in anvils) return
        event.isCancelled = true
        GUIManager.open(event.player, "enchant_anvil")
    }
}
