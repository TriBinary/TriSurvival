package net.trilleo.mc.plugins.trisurvival.items

import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object AbilityCooldownManager {

    private val cooldowns = ConcurrentHashMap<UUID, MutableMap<String, Long>>()

    fun isOnCooldown(player: Player, abilityKey: String): Boolean =
        getRemainingMillis(player, abilityKey) > 0

    fun setCooldown(player: Player, abilityKey: String, ticks: Int) {
        val expiry = System.currentTimeMillis() + (ticks * 50L)
        cooldowns.getOrPut(player.uniqueId) { mutableMapOf() }[abilityKey] = expiry
    }

    fun getRemainingMillis(player: Player, abilityKey: String): Long {
        val expiry = cooldowns[player.uniqueId]?.get(abilityKey) ?: return 0
        return (expiry - System.currentTimeMillis()).coerceAtLeast(0)
    }

    fun getRemainingSeconds(player: Player, abilityKey: String): Double =
        getRemainingMillis(player, abilityKey) / 1000.0

    fun cleanup(player: Player) {
        cooldowns.remove(player.uniqueId)
    }

    fun abilityKey(itemId: String, abilityName: String): String =
        "$itemId:$abilityName"
}
