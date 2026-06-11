package net.trilleo.mc.plugins.trisurvival.listeners.mining

import net.trilleo.mc.plugins.trisurvival.mining.BreakEffects
import net.trilleo.mc.plugins.trisurvival.mining.MiningSessions
import net.trilleo.mc.plugins.trisurvival.stats.BlockStrength
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import net.trilleo.mc.plugins.trisurvival.stats.ToolSpeed
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDamageAbortEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Drives the custom mining engine. Vanilla block-breaking is neutralised (BLOCK_BREAK_SPEED is zeroed
 * in StatManager), so every survival break is governed here: a [BlockDamageEvent] computes the break
 * time from Block Strength and Mining Speed and opens a session that [MiningTickTask] advances.
 */
class MiningStartListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onDamage(event: BlockDamageEvent) {
        val player = event.player
        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) return

        // Instant-mineable vanilla blocks (hardness 0) are broken by vanilla in this same tick.
        if (event.instaBreak) return

        val block = event.block
        val strength = BlockStrength.strengthOf(block)
        if (strength == BlockStrength.UNBREAKABLE) return

        val tool = player.inventory.itemInMainHand
        val miningSpeed = StatManager.getStat(player, Stat.MINING_SPEED) + ToolSpeed.contribution(tool, block)
        val ticks = BlockStrength.miningTimeTicks(strength, miningSpeed, BlockStrength.isOre(block))

        if (ticks == 0) {
            MiningSessions.cancel(player)
            val data = block.blockData
            if (player.breakBlock(block)) BreakEffects.play(block, data)
            return
        }

        MiningSessions.start(player, block.location, ticks)
    }

    @EventHandler
    fun onAbort(event: BlockDamageAbortEvent) {
        MiningSessions.cancel(event.player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        MiningSessions.remove(event.player.uniqueId)
    }
}
