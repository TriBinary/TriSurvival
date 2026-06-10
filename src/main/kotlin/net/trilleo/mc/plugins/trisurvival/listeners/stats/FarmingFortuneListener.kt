package net.trilleo.mc.plugins.trisurvival.listeners.stats

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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onDrop(event: BlockDropItemEvent) {
        if (event.blockState.type !in cropTypes) return

        val blockData = event.blockState.blockData
        if (blockData is Ageable && blockData.age < blockData.maximumAge) return

        val fortune = StatManager.getStat(event.player, Stat.FARMING_FORTUNE)
        val extra = FortuneUtil.rollFortune(fortune)
        if (extra <= 0) return

        for (item in event.items.toList()) {
            repeat(extra) {
                event.block.world.dropItemNaturally(event.block.location, item.itemStack.clone())
            }
        }
    }
}
