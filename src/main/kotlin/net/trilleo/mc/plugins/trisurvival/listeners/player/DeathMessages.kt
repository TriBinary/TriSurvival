package net.trilleo.mc.plugins.trisurvival.listeners.player

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Tracks the most recent fatal-damage source per player so [PlayerLifeListener] can render an
 * accurate death message. Custom damage is dealt by setting `player.health = 0.0` directly, which
 * wipes the vanilla last-damage cause and leaves Minecraft with only the generic "<player> died"
 * fallback — we reconstruct the real cause here instead.
 */
object DeathMessages {

    private val mm = MiniMessage.miniMessage()
    private val lastMessage = ConcurrentHashMap<UUID, String>()

    fun recordKilledBy(victim: Player, sourceName: String) {
        lastMessage[victim.uniqueId] =
            "<red>${victim.name}</red> <gray>was slain by</gray> <red>$sourceName</red><gray>.</gray>"
    }

    fun recordEnvironmental(victim: Player, cause: DamageCause) {
        lastMessage[victim.uniqueId] = environmental(victim.name, cause)
    }

    fun consume(victim: Player): Component {
        val raw = lastMessage.remove(victim.uniqueId)
            ?: "<red>${victim.name}</red> <gray>died.</gray>"
        return mm.deserialize(raw)
    }

    fun clear(victim: Player) {
        lastMessage.remove(victim.uniqueId)
    }

    private fun environmental(name: String, cause: DamageCause): String {
        val tail = when (cause) {
            DamageCause.FALL, DamageCause.FLY_INTO_WALL -> "fell to their death"
            DamageCause.FIRE, DamageCause.FIRE_TICK, DamageCause.LAVA, DamageCause.HOT_FLOOR,
            DamageCause.CAMPFIRE -> "burned to death"
            DamageCause.DROWNING -> "drowned"
            DamageCause.VOID -> "fell into the void"
            DamageCause.SUFFOCATION -> "suffocated"
            DamageCause.STARVATION -> "starved to death"
            DamageCause.BLOCK_EXPLOSION, DamageCause.ENTITY_EXPLOSION -> "blew up"
            DamageCause.LIGHTNING -> "was struck by lightning"
            DamageCause.FREEZE -> "froze to death"
            DamageCause.DRYOUT -> "dried out"
            else -> "died"
        }
        return "<red>$name</red> <gray>$tail.</gray>"
    }
}
