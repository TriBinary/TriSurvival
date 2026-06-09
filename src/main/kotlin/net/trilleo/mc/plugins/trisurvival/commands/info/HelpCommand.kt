package net.trilleo.mc.plugins.trisurvival.commands.info

import net.trilleo.mc.plugins.trisurvival.registration.CommandRegistrar
import net.trilleo.mc.plugins.trisurvival.registration.PluginCommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.CommandSender

class HelpCommand : PluginCommand(
    name = "help",
    description = "Show all available commands"
) {
    companion object {
        private const val HEADER_WIDTH = 42
    }

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        val categorized = CommandRegistrar.getCommandsByCategory()

        sender.sendMessage(
            Component.text("=========", NamedTextColor.GOLD)
                .append(
                    Component.text(" TriSurvival Commands ", NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD)
                )
                .append(Component.text("=========", NamedTextColor.GOLD))
        )

        for ((category, commands) in categorized) {
            sender.sendMessage(
                Component.text("» ", NamedTextColor.GOLD)
                    .append(
                        Component.text(category, NamedTextColor.YELLOW)
                            .decorate(TextDecoration.BOLD)
                    )
            )

            for (info in commands) {
                val commandText = if (info.isSubCommand) {
                    "/trisurvival ${info.command.name}"
                } else {
                    "/${info.command.name}"
                }
                sender.sendMessage(
                    Component.text("  $commandText", NamedTextColor.GREEN)
                        .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(info.command.description, NamedTextColor.GRAY))
                )
            }
        }

        sender.sendMessage(Component.text("=".repeat(HEADER_WIDTH), NamedTextColor.GOLD))

        return true
    }
}
