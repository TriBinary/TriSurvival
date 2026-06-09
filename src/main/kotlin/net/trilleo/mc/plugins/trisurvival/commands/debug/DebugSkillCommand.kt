package net.trilleo.mc.plugins.trisurvival.commands.debug

import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.registration.PluginCommand
import net.trilleo.mc.plugins.trisurvival.skills.Skill
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DebugSkillCommand : PluginCommand(
    name = "debug",
    description = "Debug commands for skills",
    usage = "/ts debug <addxp|setlevel> <skill> <amount>",
    permission = "trisurvival.debug"
) {
    private val mm = MiniMessage.miniMessage()

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be used by players.")
            return true
        }

        if (args.size < 3) {
            sender.sendMessage(mm.deserialize("<red>Usage: /ts debug <addxp|setlevel> <skill> <amount>"))
            return true
        }

        val action = args[0].lowercase()
        val skill = runCatching { Skill.valueOf(args[1].uppercase()) }.getOrNull()
        if (skill == null) {
            sender.sendMessage(mm.deserialize("<red>Unknown skill: ${args[1]}"))
            return true
        }

        val amount = args[2].toDoubleOrNull()
        if (amount == null) {
            sender.sendMessage(mm.deserialize("<red>Invalid amount: ${args[2]}"))
            return true
        }

        when (action) {
            "addxp" -> {
                SkillManager.addXP(sender, skill, amount)
                sender.sendMessage(mm.deserialize("<green>Added ${amount} XP to ${skill.displayName}"))
            }
            else -> {
                sender.sendMessage(mm.deserialize("<red>Unknown action: $action. Use: addxp"))
            }
        }

        return true
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>): List<String> = when (args.size) {
        1 -> listOf("addxp").filter { it.startsWith(args[0].lowercase()) }
        2 -> Skill.entries.map { it.name.lowercase() }.filter { it.startsWith(args[1].lowercase()) }
        3 -> listOf("10", "100", "1000")
        else -> emptyList()
    }
}
