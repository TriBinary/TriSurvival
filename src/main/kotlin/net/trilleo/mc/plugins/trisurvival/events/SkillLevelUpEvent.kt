package net.trilleo.mc.plugins.trisurvival.events

import net.trilleo.mc.plugins.trisurvival.skills.Skill
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class SkillLevelUpEvent(
    val player: Player,
    val skill: Skill,
    val oldLevel: Int,
    val newLevel: Int
) : Event() {

    override fun getHandlers(): HandlerList = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
