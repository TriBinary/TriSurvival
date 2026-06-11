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
        player.sendBlockDamage(session.location, 0f)
    }

    fun remove(uuid: UUID) {
        sessions.remove(uuid)
    }
}
