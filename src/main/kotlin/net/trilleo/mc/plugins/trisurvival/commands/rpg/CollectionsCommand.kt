package net.trilleo.mc.plugins.trisurvival.commands.rpg

import net.trilleo.mc.plugins.trisurvival.registration.GUIManager
import net.trilleo.mc.plugins.trisurvival.registration.PluginCommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CollectionsCommand : PluginCommand(
    name = "collections",
    description = "View your resource collections",
    usage = "/collections",
    aliases = listOf("collection", "col"),
    isMainCommand = true
) {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be used by players.")
            return true
        }
        GUIManager.open(sender, "collections")
        return true
    }
}
