package net.trilleo.mc.plugins.trisurvival.tasks

import net.trilleo.mc.plugins.trisurvival.registration.PluginTask
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute

class HealthRegenTask : PluginTask(delay = 20L, period = 20L) {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.isDead) continue
            val regen = StatManager.getStat(player, Stat.HEALTH_REGEN)
            if (regen <= 0) continue
            val heartsToHeal = regen / 5.0
            val maxHealth = player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
            if (player.health < maxHealth) {
                player.health = (player.health + heartsToHeal).coerceAtMost(maxHealth)
            }
        }
    }
}
