package net.trilleo.mc.plugins.trisurvival.commands.rpg

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.registration.PluginCommand
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatCategory
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class StatsCommand : PluginCommand(
    name = "stats",
    description = "View your current stats",
    usage = "/ts stats"
) {
    private val mm = MiniMessage.miniMessage()

    private val percentageStats = setOf(
        Stat.CRIT_CHANCE, Stat.CRIT_DAMAGE,
        Stat.SEA_CREATURE_CHANCE, Stat.TREASURE_CHANCE, Stat.DOUBLE_HOOK_CHANCE,
        Stat.COMBAT_WISDOM, Stat.MINING_WISDOM, Stat.FARMING_WISDOM,
        Stat.FORAGING_WISDOM, Stat.FISHING_WISDOM, Stat.ENCHANTING_WISDOM,
        Stat.ALCHEMY_WISDOM
    )

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be used by players.")
            return true
        }

        val profile = StatManager.getProfile(sender)

        sender.sendMessage(Component.empty())
        sender.sendMessage(mm.deserialize("<gold><bold>Your Stats"))

        for (category in StatCategory.entries.sortedBy { it.order }) {
            val statsInCategory = Stat.entries.filter { it.category == category }
            val nonZeroStats = statsInCategory.filter { profile[it] != 0.0 || it.baseValue != 0.0 }
            if (nonZeroStats.isEmpty()) continue

            sender.sendMessage(Component.empty())
            sender.sendMessage(mm.deserialize("<gray><bold> ${category.displayName}"))

            for (stat in nonZeroStats) {
                val value = profile[stat]
                val suffix = if (stat in percentageStats) "%" else ""
                sender.sendMessage(
                    mm.deserialize("  ${stat.color}${stat.symbol} ${stat.displayName}: <white>${formatNumber(value)}${suffix}")
                )
            }
        }

        sender.sendMessage(Component.empty())
        return true
    }

    private fun formatNumber(value: Double): String =
        if (value == value.toLong().toDouble()) value.toLong().toString()
        else String.format("%.1f", value)
}
