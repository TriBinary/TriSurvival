package net.trilleo.mc.plugins.trisurvival.commands.rpg

import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.mobs.runtime.MobManager
import net.trilleo.mc.plugins.trisurvival.mobs.spawn.MobSpawnRegistry
import net.trilleo.mc.plugins.trisurvival.mobs.spawn.MobZone
import net.trilleo.mc.plugins.trisurvival.registration.MobRegistrar
import net.trilleo.mc.plugins.trisurvival.registration.PluginCommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/** Admin command for spawning custom mobs and managing spawn zones. */
class MobCommand : PluginCommand(
    name = "mob",
    description = "Manage custom mobs",
    usage = "/mob <spawn|list|zone>",
    isMainCommand = true
) {

    private val mm = MiniMessage.miniMessage()

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (!sender.isOp) {
            sender.sendMessage(mm.deserialize("<red>You don't have permission to use this command."))
            return true
        }
        if (args.isEmpty()) {
            sendUsage(sender)
            return true
        }

        when (args[0].lowercase()) {
            "list" -> {
                val ids = MobRegistrar.getAll().sortedBy { it.id }
                if (ids.isEmpty()) {
                    sender.sendMessage(mm.deserialize("<gray>No custom mobs registered."))
                } else {
                    sender.sendMessage(mm.deserialize("<gold>Custom mobs (${ids.size}):"))
                    ids.forEach {
                        sender.sendMessage(mm.deserialize("<gray>- <white>${it.id} ${it.rarity.color}(${it.rarity.name})"))
                    }
                }
            }

            "spawn" -> {
                if (sender !is Player) {
                    sender.sendMessage(mm.deserialize("<red>Only players can spawn mobs."))
                    return true
                }
                if (args.size < 2) {
                    sender.sendMessage(mm.deserialize("<red>Usage: /mob spawn <id> [amount]"))
                    return true
                }
                val def = MobRegistrar.get(args[1])
                if (def == null) {
                    sender.sendMessage(mm.deserialize("<red>Unknown mob ID '${args[1]}'."))
                    return true
                }
                val amount = args.getOrNull(2)?.toIntOrNull()?.coerceIn(1, 50) ?: 1
                var spawned = 0
                repeat(amount) { if (def.spawn(sender.location) != null) spawned++ }
                sender.sendMessage(mm.deserialize("<green>Spawned $spawned ${def.rarity.color}${def.displayName}<green>."))
            }

            "zone" -> handleZone(sender, args)

            else -> sendUsage(sender)
        }
        return true
    }

    private fun handleZone(sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage(mm.deserialize("<red>Usage: /mob zone <add|remove|list>"))
            return
        }
        when (args[1].lowercase()) {
            "list" -> {
                val zones = MobSpawnRegistry.zones()
                if (zones.isEmpty()) {
                    sender.sendMessage(mm.deserialize("<gray>No spawn zones configured."))
                } else {
                    sender.sendMessage(mm.deserialize("<gold>Spawn zones (${zones.size}):"))
                    zones.forEach {
                        sender.sendMessage(
                            mm.deserialize("<gray>- <white>${it.name} <gray>(${it.world}, r=${it.radius.toInt()}, cap=${it.cap}) <dark_gray>${it.mobIds.joinToString(",")}")
                        )
                    }
                }
            }

            "add" -> {
                if (sender !is Player) {
                    sender.sendMessage(mm.deserialize("<red>Only players can add a zone (uses your location)."))
                    return
                }
                if (args.size < 6) {
                    sender.sendMessage(mm.deserialize("<red>Usage: /mob zone add <name> <radius> <cap> <id> [id...]"))
                    return
                }
                val name = args[2]
                val radius = args[3].toDoubleOrNull()
                val cap = args[4].toIntOrNull()
                if (radius == null || cap == null) {
                    sender.sendMessage(mm.deserialize("<red>Radius and cap must be numbers."))
                    return
                }
                val ids = args.drop(5)
                val unknown = ids.filter { MobRegistrar.get(it) == null }
                if (unknown.isNotEmpty()) {
                    sender.sendMessage(mm.deserialize("<red>Unknown mob ID(s): ${unknown.joinToString(", ")}"))
                    return
                }
                val loc = sender.location
                MobSpawnRegistry.addZone(
                    MobZone(name, loc.world.name, loc.x, loc.y, loc.z, radius, cap, ids)
                )
                sender.sendMessage(mm.deserialize("<green>Added spawn zone '<white>$name<green>'."))
            }

            "remove" -> {
                if (args.size < 3) {
                    sender.sendMessage(mm.deserialize("<red>Usage: /mob zone remove <name>"))
                    return
                }
                if (MobSpawnRegistry.removeZone(args[2])) {
                    sender.sendMessage(mm.deserialize("<green>Removed spawn zone '<white>${args[2]}<green>'."))
                } else {
                    sender.sendMessage(mm.deserialize("<red>No zone named '${args[2]}'."))
                }
            }

            else -> sender.sendMessage(mm.deserialize("<red>Usage: /mob zone <add|remove|list>"))
        }
    }

    private fun sendUsage(sender: CommandSender) {
        sender.sendMessage(mm.deserialize("<gold>/mob <gray>- custom mob admin"))
        sender.sendMessage(mm.deserialize("<gray>  /mob spawn <id> [amount]"))
        sender.sendMessage(mm.deserialize("<gray>  /mob list"))
        sender.sendMessage(mm.deserialize("<gray>  /mob zone <add|remove|list>"))
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> listOf("spawn", "list", "zone").filter { it.startsWith(args[0].lowercase()) }
            2 -> when (args[0].lowercase()) {
                "spawn" -> MobRegistrar.getAll().map { it.id }.filter { it.startsWith(args[1].lowercase()) }
                "zone" -> listOf("add", "remove", "list").filter { it.startsWith(args[1].lowercase()) }
                else -> emptyList()
            }
            else -> when {
                args[0].equals("spawn", true) -> emptyList()
                args[0].equals("zone", true) && args[1].equals("add", true) && args.size >= 6 ->
                    MobRegistrar.getAll().map { it.id }.filter { it.startsWith(args.last().lowercase()) }
                args[0].equals("zone", true) && args[1].equals("remove", true) && args.size == 3 ->
                    MobSpawnRegistry.zones().map { it.name }.filter { it.startsWith(args[2], ignoreCase = true) }
                else -> emptyList()
            }
        }
    }
}
