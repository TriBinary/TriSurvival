package net.trilleo.mc.plugins.trisurvival.listeners.skills

import net.trilleo.mc.plugins.trisurvival.skills.Skill
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class FarmingXPListener : Listener {

    private val xpValues = mapOf(
        Material.WHEAT to 8.0,
        Material.CARROTS to 8.0,
        Material.POTATOES to 8.0,
        Material.BEETROOTS to 8.0,
        Material.NETHER_WART to 10.0,
        Material.MELON to 6.0,
        Material.PUMPKIN to 6.0,
        Material.SUGAR_CANE to 4.0,
        Material.CACTUS to 4.0,
        Material.COCOA to 6.0,
        Material.SWEET_BERRY_BUSH to 5.0,
        Material.RED_MUSHROOM to 5.0,
        Material.BROWN_MUSHROOM to 5.0
    )

    private val ageableTypes = setOf(
        Material.WHEAT, Material.CARROTS, Material.POTATOES,
        Material.BEETROOTS, Material.NETHER_WART, Material.COCOA,
        Material.SWEET_BERRY_BUSH
    )

    private val placedFilterTypes = setOf(
        Material.CACTUS, Material.SUGAR_CANE,
        Material.RED_MUSHROOM, Material.BROWN_MUSHROOM
    )

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val type = event.block.type
        val xp = xpValues[type] ?: return

        if (type in ageableTypes) {
            val ageable = event.block.blockData as? Ageable ?: return
            if (ageable.age < ageable.maximumAge) return
        }

        if (type in placedFilterTypes && BlockPlaceTracker.isPlayerPlaced(event.block)) return

        SkillManager.addXP(event.player, Skill.FARMING, xp)
    }
}
