package net.trilleo.mc.plugins.trisurvival.events

import net.trilleo.mc.plugins.trisurvival.stats.StatProfile
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class StatRecalcEvent(
    val player: Player,
    val profile: StatProfile
) : Event() {

    override fun getHandlers(): HandlerList = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
