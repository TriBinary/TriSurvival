package net.trilleo.mc.plugins.trisurvival.events

import net.trilleo.mc.plugins.trisurvival.mobs.CustomMob
import org.bukkit.Location
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/** Fired before a [CustomMob] is spawned. Cancelling it aborts the spawn. */
class CustomMobSpawnEvent(
    val def: CustomMob,
    val location: Location
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
