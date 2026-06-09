package net.trilleo.mc.plugins.trisurvival.listeners.skills

import net.trilleo.mc.plugins.trisurvival.skills.Skill
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class MiningXPListener : Listener {

    private val xpValues = mapOf(
        Material.COAL_ORE to 5.0,
        Material.DEEPSLATE_COAL_ORE to 6.0,
        Material.COPPER_ORE to 6.0,
        Material.DEEPSLATE_COPPER_ORE to 7.0,
        Material.IRON_ORE to 8.0,
        Material.DEEPSLATE_IRON_ORE to 9.0,
        Material.GOLD_ORE to 12.0,
        Material.DEEPSLATE_GOLD_ORE to 13.0,
        Material.NETHER_GOLD_ORE to 10.0,
        Material.REDSTONE_ORE to 10.0,
        Material.DEEPSLATE_REDSTONE_ORE to 11.0,
        Material.LAPIS_ORE to 12.0,
        Material.DEEPSLATE_LAPIS_ORE to 13.0,
        Material.DIAMOND_ORE to 20.0,
        Material.DEEPSLATE_DIAMOND_ORE to 22.0,
        Material.EMERALD_ORE to 25.0,
        Material.DEEPSLATE_EMERALD_ORE to 27.0,
        Material.NETHER_QUARTZ_ORE to 8.0,
        Material.ANCIENT_DEBRIS to 50.0,
        Material.STONE to 1.0,
        Material.DEEPSLATE to 1.5,
        Material.NETHERRACK to 1.0,
        Material.END_STONE to 2.0,
        Material.OBSIDIAN to 10.0
    )

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        if (BlockPlaceTracker.isPlayerPlaced(event.block)) return
        val xp = xpValues[event.block.type] ?: return
        SkillManager.addXP(event.player, Skill.MINING, xp)
    }
}
