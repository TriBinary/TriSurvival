package net.trilleo.mc.plugins.trisurvival.listeners.skills

import net.trilleo.mc.plugins.trisurvival.skills.Skill
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
import org.bukkit.block.BrewingStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.BrewEvent

class AlchemyXPListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onBrew(event: BrewEvent) {
        val stand = event.block.state as? BrewingStand ?: return
        val viewers = stand.inventory.viewers
        val player = viewers.filterIsInstance<Player>().firstOrNull()

        if (player != null) {
            SkillManager.addXP(player, Skill.ALCHEMY, 15.0)
        }
    }
}
