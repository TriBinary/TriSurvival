package net.trilleo.mc.plugins.trisurvival.tasks

import net.trilleo.mc.plugins.trisurvival.registration.PluginTask
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.Bukkit

class HealthRegenTask : PluginTask(delay = 40L, period = 40L) {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.isDead) continue
            val profile = StatManager.getProfile(player)
            val healthRegen = profile[Stat.HEALTH_REGEN]
            if (healthRegen <= 0) continue
            if (profile.currentHealth >= profile.health) continue

            val heal = (profile.health / 100.0 + 1.5) * (healthRegen / 100.0)
            val vitality = profile[Stat.VITALITY]
            val adjustedHeal = heal * (vitality / 100.0)

            profile.currentHealth = (profile.currentHealth + adjustedHeal).coerceAtMost(profile.health)
            StatManager.syncVanillaHealth(player)
        }
    }
}
