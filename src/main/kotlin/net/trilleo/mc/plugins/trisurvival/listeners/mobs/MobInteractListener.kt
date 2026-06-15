package net.trilleo.mc.plugins.trisurvival.listeners.mobs

import net.trilleo.mc.plugins.trisurvival.mobs.CustomMob
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.EquipmentSlot

/** Stops players renaming custom mobs with a name tag — the hologram is the only label they get. */
class MobInteractListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onInteract(event: PlayerInteractEntityEvent) {
        if (!CustomMob.isCustom(event.rightClicked)) return
        val hand = if (event.hand == EquipmentSlot.HAND) {
            event.player.inventory.itemInMainHand
        } else {
            event.player.inventory.itemInOffHand
        }
        if (hand.type == Material.NAME_TAG) event.isCancelled = true
    }
}
