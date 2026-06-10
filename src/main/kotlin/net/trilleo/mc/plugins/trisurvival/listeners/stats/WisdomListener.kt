package net.trilleo.mc.plugins.trisurvival.listeners.stats

import net.trilleo.mc.plugins.trisurvival.events.SkillXPGainEvent
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class WisdomListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onXPGain(event: SkillXPGainEvent) {
        val wisdomStat = Stat.wisdomForSkill(event.skill)
        val wisdom = StatManager.getStat(event.player, wisdomStat)
        if (wisdom > 0) {
            event.amount *= (1 + wisdom / 100.0)
        }
    }
}
