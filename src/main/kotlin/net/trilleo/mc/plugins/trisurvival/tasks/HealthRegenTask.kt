package net.trilleo.mc.plugins.trisurvival.tasks

import net.trilleo.mc.plugins.trisurvival.listeners.stats.HealthListener
import net.trilleo.mc.plugins.trisurvival.registration.PluginTask
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute

class HealthRegenTask : PluginTask(delay = 20L, period = 20L) {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.isDead) continue
            val profile = StatManager.getProfile(player)
            val regen = profile[Stat.HEALTH_REGEN]
            if (regen <= 0) continue
            val maxHearts = player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
            val regenHearts = regen / 5.0 * (maxHearts / (profile.health / 5.0).coerceAtLeast(2.0))
            if (player.health < maxHearts) {
                player.health = (player.health + regenHearts).coerceAtMost(maxHearts)
            }
        }
    }
}
