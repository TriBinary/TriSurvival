package net.trilleo.mc.plugins.trisurvival.events

import net.trilleo.mc.plugins.trisurvival.skills.Skill
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class SkillXPGainEvent(
    val player: Player,
    val skill: Skill,
    var amount: Double
) : Event(), Cancellable {

    private var cancelled = false

    override fun isCancelled(): Boolean = cancelled
    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    override fun getHandlers(): HandlerList = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
