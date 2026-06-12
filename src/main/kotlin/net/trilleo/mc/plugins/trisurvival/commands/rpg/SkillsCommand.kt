package net.trilleo.mc.plugins.trisurvival.commands.rpg

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.registration.GUIManager
import net.trilleo.mc.plugins.trisurvival.registration.PluginCommand
import net.trilleo.mc.plugins.trisurvival.skills.Skill
import net.trilleo.mc.plugins.trisurvival.skills.SkillConfig
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SkillsCommand : PluginCommand(
    name = "skills",
    description = "View your skill levels and XP",
    usage = "/skills [skill]",
    aliases = listOf("skill"),
    isMainCommand = true
) {
    private val mm = MiniMessage.miniMessage()

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be used by players.")
            return true
        }

        if (args.isEmpty()) {
            GUIManager.open(sender, "skills")
            return true
        }

        val skillName = args[0].uppercase()
        val skill = runCatching { Skill.valueOf(skillName) }.getOrNull()
        if (skill == null) {
            sender.sendMessage(mm.deserialize("<red>Unknown skill: ${args[0]}"))
            return true
        }

        val level = SkillManager.getLevel(sender.uniqueId, skill)
        val xp = SkillManager.getXP(sender.uniqueId, skill)
        val progress = SkillManager.getProgress(sender.uniqueId, skill) * 100

        sender.sendMessage(Component.empty())
        sender.sendMessage(
            mm.deserialize("<gold><bold>${skill.displayName} <gray>Skill")
        )
        sender.sendMessage(
            mm.deserialize("  <yellow>Level: <white>${level}<gray>/${skill.maxLevel}")
        )
        sender.sendMessage(
            mm.deserialize("  <yellow>XP: <white>${formatNumber(xp)}<gray>/${formatNumber(skill.totalXpForLevel(level + 1))}")
        )
        sender.sendMessage(
            mm.deserialize("  <yellow>Progress: <white>${String.format("%.1f", progress)}%")
        )

        val progressBar = buildProgressBar(progress / 100.0)
        sender.sendMessage(mm.deserialize("  $progressBar"))

        sender.sendMessage(Component.empty())
        sender.sendMessage(mm.deserialize("  <gold>Rewards per level:"))
        val reward = SkillConfig.getReward(skill)
        for ((stat, bonus) in reward.statBonuses) {
            sender.sendMessage(
                mm.deserialize("    ${stat.color}${stat.symbol} +${formatNumber(bonus)} ${stat.displayName}")
            )
        }

        val nextMilestone = reward.milestones.keys.filter { it > level }.minOrNull()
        if (nextMilestone != null) {
            val milestone = reward.milestones[nextMilestone]!!
            sender.sendMessage(Component.empty())
            sender.sendMessage(
                mm.deserialize("  <light_purple>Next Milestone (Level ${nextMilestone}): <white>${milestone.description}")
            )
        }

        sender.sendMessage(Component.empty())
        return true
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        if (args.size == 1) {
            return Skill.entries.map { it.name.lowercase() }
                .filter { it.startsWith(args[0].lowercase()) }
        }
        return emptyList()
    }

    private fun buildProgressBar(progress: Double): String {
        val total = 20
        val filled = (progress * total).toInt().coerceIn(0, total)
        val empty = total - filled
        return "<green>${"■".repeat(filled)}<gray>${"■".repeat(empty)}"
    }

    private fun formatNumber(value: Double): String =
        if (value == value.toLong().toDouble()) value.toLong().toString()
        else String.format("%.1f", value)
}
