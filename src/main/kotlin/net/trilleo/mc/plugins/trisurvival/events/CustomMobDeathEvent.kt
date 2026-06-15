package net.trilleo.mc.plugins.trisurvival.events

import net.trilleo.mc.plugins.trisurvival.mobs.CustomMob
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/** Fired when a [CustomMob] dies, after drops and XP have been resolved. */
class CustomMobDeathEvent(
    val def: CustomMob,
    val entity: LivingEntity,
    val killer: Player?
) : Event() {

    override fun getHandlers(): HandlerList = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
