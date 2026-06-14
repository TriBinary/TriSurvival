package net.trilleo.mc.plugins.trisurvival.commands.rpg

import net.trilleo.mc.plugins.trisurvival.registration.GUIManager
import net.trilleo.mc.plugins.trisurvival.registration.PluginCommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class StatsCommand : PluginCommand(
    name = "stats",
    description = "View your current stats",
    usage = "/stats",
    isMainCommand = true
) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be used by players.")
            return true
        }
        GUIManager.open(sender, "stats")
        return true
    }
}
