package net.trilleo.mc.plugins.trisurvival.listeners.mining

import net.trilleo.mc.plugins.trisurvival.listeners.skills.BlockPlaceTracker
import net.trilleo.mc.plugins.trisurvival.ores.CustomOreRewards
import net.trilleo.mc.plugins.trisurvival.ores.CustomOres
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

/**
 * Turns a broken custom-ore block into its custom drops, Mining Fortune extras, skill XP and XP orbs,
 * suppressing the vanilla representing-block drop. Fires for direct breaks; Mining Spread handles its
 * own chained custom-ore breaks via [CustomOreRewards] directly.
 */
class CustomOreBreakListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBreak(event: BlockBreakEvent) {
        val block = event.block
        val ore = CustomOres.oreAt(block) ?: return

        if (BlockPlaceTracker.isPlayerPlaced(block)) {
            CustomOres.clearMark(block)
            return
        }

        event.isDropItems = false
        event.expToDrop = 0
        CustomOreRewards.give(block, event.player, ore, event.player.inventory.itemInMainHand)
        CustomOres.clearMark(block)
    }
}
