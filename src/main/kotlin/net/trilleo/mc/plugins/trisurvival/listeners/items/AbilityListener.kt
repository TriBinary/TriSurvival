package net.trilleo.mc.plugins.trisurvival.listeners.items

import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.items.AbilityCooldownManager
import net.trilleo.mc.plugins.trisurvival.items.AbilityTrigger
import net.trilleo.mc.plugins.trisurvival.items.ItemAbility
import net.trilleo.mc.plugins.trisurvival.registration.ItemRegistrar
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import net.trilleo.mc.plugins.trisurvival.utils.PDCUtil
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType

class AbilityListener : Listener {

    private val mm = MiniMessage.miniMessage()

    @EventHandler(priority = EventPriority.NORMAL)
    fun onInteract(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND) return

        val trigger = when (event.action) {
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> AbilityTrigger.RIGHT_CLICK
            Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> AbilityTrigger.LEFT_CLICK
            else -> return
        }

        val player = event.player
        val item = player.inventory.itemInMainHand
        val itemId = PDCUtil.get(item, PluginItem.ITEM_ID_KEY, PersistentDataType.STRING) ?: return
        val pluginItem = ItemRegistrar.get(itemId) ?: return

        for (ability in pluginItem.abilities) {
            if (ability.trigger == trigger) {
                executeAbility(player, pluginItem, ability)
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onSneak(event: PlayerToggleSneakEvent) {
        if (!event.isSneaking) return

        val player = event.player
        val item = player.inventory.itemInMainHand
        val itemId = PDCUtil.get(item, PluginItem.ITEM_ID_KEY, PersistentDataType.STRING) ?: return
        val pluginItem = ItemRegistrar.get(itemId) ?: return

        for (ability in pluginItem.abilities) {
            if (ability.trigger == AbilityTrigger.SNEAK) {
                executeAbility(player, pluginItem, ability)
            }
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        AbilityCooldownManager.cleanup(event.player)
    }

    private fun executeAbility(player: Player, pluginItem: PluginItem, ability: ItemAbility) {
        val abilityKey = AbilityCooldownManager.abilityKey(pluginItem.id, ability.name)

        if (AbilityCooldownManager.isOnCooldown(player, abilityKey)) {
            val remaining = AbilityCooldownManager.getRemainingSeconds(player, abilityKey)
            player.sendActionBar(
                mm.deserialize("<red>This ability is on cooldown for <white>%.1fs".format(remaining))
            )
            return
        }

        val profile = StatManager.getProfile(player)
        if (ability.manaCost > 0 && profile.currentMana < ability.manaCost) {
            player.sendActionBar(
                mm.deserialize("<red>Not enough mana! Need <dark_aqua>${ability.manaCost} Mana")
            )
            return
        }

        if (ability.manaCost > 0) {
            profile.currentMana -= ability.manaCost
        }

        if (ability.cooldownTicks > 0) {
            AbilityCooldownManager.setCooldown(player, abilityKey, ability.cooldownTicks)
        }

        ability.action(player)

        val remainingMana = profile.currentMana.toLong()
        player.sendActionBar(
            mm.deserialize("<aqua>Used <gold>${ability.name}<aqua>! (<dark_aqua>$remainingMana Mana<aqua>)")
        )
    }
}
