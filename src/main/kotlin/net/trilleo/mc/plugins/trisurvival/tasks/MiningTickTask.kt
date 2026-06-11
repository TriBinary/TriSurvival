package net.trilleo.mc.plugins.trisurvival.tasks

import net.trilleo.mc.plugins.trisurvival.mining.BreakEffects
import net.trilleo.mc.plugins.trisurvival.mining.MiningSessions
import net.trilleo.mc.plugins.trisurvival.registration.PluginTask
import org.bukkit.Bukkit

/**
 * Advances every active mining session by one tick: shows the crack animation and, once the computed
 * break time elapses, breaks the block via the player so all normal break events fire.
 */
class MiningTickTask : PluginTask(period = 1L) {

    override fun run() {
        for ((uuid, session) in MiningSessions.entries()) {
            val player = Bukkit.getPlayer(uuid)
            if (player == null) {
                MiningSessions.remove(uuid)
                continue
            }

            val block = session.location.block
            if (block.type.isAir) {
                MiningSessions.cancel(player)
                continue
            }

            session.elapsed++
            if (session.elapsed >= session.totalTicks) {
                MiningSessions.remove(uuid)
                val data = block.blockData
                val broke = player.breakBlock(block)
                MiningSessions.clearOverlay(player, session.location)
                if (broke) BreakEffects.play(block, data)
            } else {
                // The crack overlay has only 10 stages (0-9); only resend when the stage actually
                // changes, otherwise the per-tick refresh restarts the overlay and flickers.
                val progress = session.elapsed.toFloat() / session.totalTicks
                val stage = (progress * 10).toInt().coerceIn(0, 9)
                if (stage != session.lastStage) {
                    session.lastStage = stage
                    player.sendBlockDamage(session.location, progress, MiningSessions.BREAK_SOURCE_ID)
                }
            }
        }
    }
}
