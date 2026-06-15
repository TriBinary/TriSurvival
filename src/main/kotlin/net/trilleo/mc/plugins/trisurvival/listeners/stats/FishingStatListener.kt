package net.trilleo.mc.plugins.trisurvival.listeners.stats

import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.fishing.SeaCreatureRegistry
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import kotlin.random.Random

class FishingStatListener : Listener {

    private val mm = MiniMessage.miniMessage()

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onFish(event: PlayerFishEvent) {
        if (event.state != PlayerFishEvent.State.CAUGHT_FISH) return
        val player = event.player
        val profile = StatManager.getProfile(player)

        val seaChance = profile[Stat.SEA_CREATURE_CHANCE]
        if (seaChance > 0 && Random.nextDouble(100.0) < seaChance) {
            val creature = SeaCreatureRegistry.rollCreature()
            if (creature != null) {
                event.isCancelled = true
                creature.spawnFromHook(event.hook.location, player)
                player.sendMessage(
                    mm.deserialize("${creature.rarity.color}A ${creature.displayName} <gray>has appeared!")
                )
                return
            }
        }

        val treasureChance = profile[Stat.TREASURE_CHANCE]
        if (treasureChance > 0 && Random.nextDouble(100.0) < treasureChance) {
            val caught = event.caught as? Item
            if (caught != null) {
                caught.itemStack = rollTreasureLoot()
                player.sendMessage(mm.deserialize("<gold>You found treasure!"))
            }
        }

        val doubleHook = profile[Stat.DOUBLE_HOOK_CHANCE]
        if (doubleHook > 0 && Random.nextDouble(100.0) < doubleHook) {
            val caught = event.caught as? Item ?: return
            val clone = caught.itemStack.clone()
            player.world.dropItemNaturally(player.location, clone)
            player.sendMessage(mm.deserialize("<blue>Double Hook!"))
        }
    }

    private fun rollTreasureLoot(): org.bukkit.inventory.ItemStack {
        val treasures = listOf(
            org.bukkit.Material.DIAMOND,
            org.bukkit.Material.EMERALD,
            org.bukkit.Material.GOLD_INGOT,
            org.bukkit.Material.IRON_INGOT,
            org.bukkit.Material.LAPIS_LAZULI,
            org.bukkit.Material.NAME_TAG,
            org.bukkit.Material.NAUTILUS_SHELL,
            org.bukkit.Material.HEART_OF_THE_SEA
        )
        val material = treasures.random()
        return org.bukkit.inventory.ItemStack(material)
    }
}
