package net.trilleo.mc.plugins.trisurvival.mining

import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Tracks the in-progress custom mining of each player. A session lives from the [BlockDamageEvent]
 * that starts a dig until the block breaks or the dig is aborted.
 */
object MiningSessions {

    /**
     * Source entity id for the crack overlay. It must differ from the player's own entity id: with
     * BLOCK_BREAK_SPEED zeroed the client still writes block-break progress under its own id every
     * tick, which would otherwise overwrite our overlay and make it flicker/vanish. A negative id
     * never collides with a real (always non-negative) entity id.
     */
    const val BREAK_SOURCE_ID = -1828

    class Session(val location: Location, val totalTicks: Int) {
        var elapsed: Int = 0

        /** Last crack stage (0-9) sent to the client; -1 until the first update. */
        var lastStage: Int = -1
    }

    private val sessions = ConcurrentHashMap<UUID, Session>()

    fun start(player: Player, location: Location, totalTicks: Int) {
        sessions[player.uniqueId] = Session(location, totalTicks)
    }

    fun entries(): List<Map.Entry<UUID, Session>> = sessions.entries.toList()

    fun cancel(player: Player) {
        val session = sessions.remove(player.uniqueId) ?: return
        clearOverlay(player, session.location)
    }

    fun remove(uuid: UUID) {
        sessions.remove(uuid)
    }

    /**
     * Removes the crack overlay from [location] by relocating our source id's progress to a point
     * below the world, where the client renders nothing. Sending stage 0 at the block itself would
     * instead leave a faint lingering crack, so we move the entry off the block entirely.
     */
    fun clearOverlay(player: Player, location: Location) {
        val world = location.world ?: return
        val voidLoc = Location(world, location.x, world.minHeight - 16.0, location.z)
        player.sendBlockDamage(voidLoc, 0f, BREAK_SOURCE_ID)
    }
}
