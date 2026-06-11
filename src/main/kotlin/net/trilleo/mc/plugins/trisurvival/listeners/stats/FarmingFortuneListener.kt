package net.trilleo.mc.plugins.trisurvival.listeners.stats

import net.trilleo.mc.plugins.trisurvival.listeners.skills.BlockPlaceTracker
import net.trilleo.mc.plugins.trisurvival.stats.FortuneUtil
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent

class FarmingFortuneListener : Listener {

    private val cropTypes = setOf(
        Material.WHEAT, Material.CARROTS, Material.POTATOES,
        Material.BEETROOTS, Material.NETHER_WART,
        Material.MELON, Material.PUMPKIN,
        Material.SUGAR_CANE, Material.CACTUS,
        Material.COCOA, Material.SWEET_BERRY_BUSH
    )

    private val placedFilterTypes = setOf(
        Material.CACTUS, Material.SUGAR_CANE
    )

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onDrop(event: BlockDropItemEvent) {
        val type = event.blockState.type
        if (type !in cropTypes) return

        val blockData = event.blockState.blockData
        if (blockData is Ageable && blockData.age < blockData.maximumAge) return

        if (type in placedFilterTypes && BlockPlaceTracker.isPlayerPlaced(event.block)) return

        val fortune = StatManager.getStat(event.player, Stat.FARMING_FORTUNE)
        FortuneUtil.dropExtra(event.block, event.items.map { it.itemStack }, fortune)
    }
}
