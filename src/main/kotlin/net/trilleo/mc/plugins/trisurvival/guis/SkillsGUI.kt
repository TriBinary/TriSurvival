package net.trilleo.mc.plugins.trisurvival.guis

import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.enums.FillMode
import net.trilleo.mc.plugins.trisurvival.registration.PluginGUI
import net.trilleo.mc.plugins.trisurvival.skills.Skill
import net.trilleo.mc.plugins.trisurvival.skills.SkillConfig
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
import net.trilleo.mc.plugins.trisurvival.utils.itemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class SkillsGUI : PluginGUI(
    id = "skills",
    title = MiniMessage.miniMessage().deserialize("<dark_gray>Skills"),
    rows = 3,
    fillMode = FillMode.DARK
) {
    private val mm = MiniMessage.miniMessage()

    private val skillSlots = mapOf(
        Skill.COMBAT to 10,
        Skill.MINING to 11,
        Skill.FARMING to 12,
        Skill.FORAGING to 13,
        Skill.FISHING to 14,
        Skill.ENCHANTING to 15,
        Skill.ALCHEMY to 16
    )

    override fun setup(player: Player, inventory: Inventory) {
        for ((skill, slot) in skillSlots) {
            val level = SkillManager.getLevel(player.uniqueId, skill)
            val progress = SkillManager.getProgress(player.uniqueId, skill)
            val xp = SkillManager.getXP(player.uniqueId, skill)
            val nextLevelXP = skill.totalXpForLevel(level + 1)
            val bar = buildProgressBar(progress)
            val reward = SkillConfig.getReward(skill)

            val loreLines = mutableListOf(
                "",
                "<gray>Level: <yellow>${level}<dark_gray>/<yellow>${skill.maxLevel}",
                "<gray>XP: <yellow>${formatNumber(xp)}<dark_gray>/<yellow>${formatNumber(nextLevelXP)}",
                bar,
                ""
            )

            loreLines.add("<gray>Rewards per level:")
            for ((stat, bonus) in reward.statBonuses) {
                loreLines.add("  ${stat.color}${stat.symbol} +${formatNumber(bonus)} ${stat.displayName}")
            }

            val nextMilestone = reward.milestones.keys.filter { it > level }.minOrNull()
            if (nextMilestone != null) {
                val milestone = reward.milestones[nextMilestone]!!
                loreLines.add("")
                loreLines.add("<light_purple>Milestone (Lv ${nextMilestone}): <white>${milestone.description}")
            }

            val item = itemStack(skill.material) {
                name("<yellow><bold>${skill.displayName} <gray>Level ${level}")
                lore(*loreLines.toTypedArray())
            }

            inventory.setItem(slot, item)
        }
    }

    private fun buildProgressBar(progress: Double): String {
        val total = 20
        val filled = (progress * total).toInt().coerceIn(0, total)
        val empty = total - filled
        return "<green>${"■".repeat(filled)}<gray>${"■".repeat(empty)} <yellow>${
            String.format(
                "%.1f",
                progress * 100
            )
        }%"
    }

    private fun formatNumber(value: Double): String =
        if (value == value.toLong().toDouble()) value.toLong().toString()
        else String.format("%.1f", value)
}
