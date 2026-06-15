package net.trilleo.mc.plugins.trisurvival.listeners.player

import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.plugin.java.JavaPlugin

class PlayerLifeListener(private val plugin: JavaPlugin) : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onDeath(event: PlayerDeathEvent) {
        event.deathMessage(DeathMessages.consume(event.entity))

        // Refill the custom pool now so the recalcs triggered by death-drop armor changes (which preserve
        // the health *ratio*) restore full health instead of re-applying the 0 HP left from this death —
        // otherwise the freshly respawned player is instantly killed again, spamming death messages.
        val profile = StatManager.getProfile(event.entity)
        profile.currentHealth = profile.health
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        val profile = StatManager.getProfile(player)
        profile.currentHealth = profile.health

        // Vanilla restores health after this event resolves; sync the custom pool a tick later so the
        // player visibly respawns at full health instead of inheriting the 0 HP left from death.
        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (player.isOnline) StatManager.syncVanillaHealth(player)
        })
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        DeathMessages.clear(event.player)
    }
}
