package net.trilleo.mc.plugins.trisurvival.commands.rpg

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.registration.PluginCommand
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class StatsCommand : PluginCommand(
    name = "stats",
    description = "View your current stats",
    usage = "/ts stats"
) {
    private val mm = MiniMessage.miniMessage()

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be used by players.")
            return true
        }

        val profile = StatManager.getProfile(sender)

        sender.sendMessage(Component.empty())
        sender.sendMessage(mm.deserialize("<gold><bold>Your Stats"))
        sender.sendMessage(Component.empty())

        for (stat in Stat.entries) {
            val value = profile[stat]
            val suffix = if (stat == Stat.CRIT_CHANCE || stat == Stat.CRIT_DAMAGE) "%" else ""
            sender.sendMessage(
                mm.deserialize("  ${stat.color}${stat.symbol} ${stat.displayName}: <white>${formatNumber(value)}${suffix}")
            )
        }

        sender.sendMessage(Component.empty())
        return true
    }

    private fun formatNumber(value: Double): String =
        if (value == value.toLong().toDouble()) value.toLong().toString()
        else String.format("%.1f", value)
}
