package net.trilleo.mc.plugins.trisurvival.ores

import net.trilleo.mc.plugins.trisurvival.registration.ItemRegistrar
import net.trilleo.mc.plugins.trisurvival.skills.Skill
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
import net.trilleo.mc.plugins.trisurvival.stats.FortuneUtil
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.block.Block
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Produces the drops, Mining Fortune extras, skill XP and experience orbs for a custom ore block.
 * Shared by [net.trilleo.mc.plugins.trisurvival.listeners.mining.CustomOreBreakListener] (direct
 * breaks) and the Mining Spread listener (chained breaks) so the reward logic lives in one place.
 */
object CustomOreRewards {

    fun give(block: Block, player: Player, ore: CustomOre, tool: ItemStack) {
        // Mining with an insufficient tool breaks the block but yields nothing.
        if (!ToolRequirement.meets(tool, ore)) return

        val drop = ore.dropItemId?.let { ItemRegistrar.get(it)?.create(ore.baseDropAmount) }
        val vanillaDrop =
            if (ore.dropVanillaItem == null) null else ItemStack(ore.dropVanillaItem!!, ore.baseDropAmount)

        if (drop != null) {
            val world = block.world
            val location = block.location
            world.dropItemNaturally(location, drop)
            FortuneUtil.dropExtra(block, listOf(drop), StatManager.getStat(player, Stat.MINING_FORTUNE))
        }

        if (vanillaDrop != null) {
            val world = block.world
            val location = block.location
            world.dropItemNaturally(location, vanillaDrop)
            FortuneUtil.dropExtra(block, listOf(vanillaDrop), StatManager.getStat(player, Stat.MINING_FORTUNE))
        }

        if (ore.skillXp > 0) SkillManager.addXP(player, Skill.MINING, ore.skillXp)

        if (ore.expOrbDrop > 0) {
            block.world.spawn(block.location.add(0.5, 0.5, 0.5), ExperienceOrb::class.java) {
                it.experience = ore.expOrbDrop
            }
        }
    }
}
