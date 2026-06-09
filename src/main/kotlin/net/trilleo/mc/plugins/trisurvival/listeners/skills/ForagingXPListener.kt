package net.trilleo.mc.plugins.trisurvival.listeners.skills

import net.trilleo.mc.plugins.trisurvival.skills.Skill
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
import org.bukkit.Tag
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class ForagingXPListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        if (BlockPlaceTracker.isPlayerPlaced(event.block)) return
        val type = event.block.type

        val xp = when {
            Tag.LOGS.isTagged(type) -> 6.0
            Tag.LEAVES.isTagged(type) -> 1.0
            else -> return
        }

        SkillManager.addXP(event.player, Skill.FORAGING, xp)
    }
}
