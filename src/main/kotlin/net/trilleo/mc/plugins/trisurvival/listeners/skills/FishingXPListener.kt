package net.trilleo.mc.plugins.trisurvival.listeners.skills

import net.trilleo.mc.plugins.trisurvival.skills.Skill
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

class FishingXPListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onFish(event: PlayerFishEvent) {
        if (event.state != PlayerFishEvent.State.CAUGHT_FISH) return
        SkillManager.addXP(event.player, Skill.FISHING, 12.0)
    }
}
