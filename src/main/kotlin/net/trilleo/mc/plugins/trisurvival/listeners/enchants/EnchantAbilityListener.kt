package net.trilleo.mc.plugins.trisurvival.listeners.enchants

import net.trilleo.mc.plugins.trisurvival.enchants.AbilityEnchant
import net.trilleo.mc.plugins.trisurvival.enchants.EnchantData
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack

/**
 * Central dispatch for [AbilityEnchant] hooks. Reads the enchants on the
 * relevant equipped item(s) and invokes the matching hook with the stored level.
 */
class EnchantAbilityListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onDamage(event: EntityDamageByEntityEvent) {
        val victim = event.entity

        val damager = event.damager
        if (damager is Player && victim is LivingEntity) {
            forEachAbility(damager.inventory.itemInMainHand) { enchant, level ->
                enchant.onAttack(damager, victim, level, event)
            }
        }

        if (victim is Player) {
            for (armor in armorOf(victim)) {
                forEachAbility(armor) { enchant, level ->
                    enchant.onDamaged(victim, damager, level, event)
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        forEachAbility(event.player.inventory.itemInMainHand) { enchant, level ->
            enchant.onBlockBreak(event.player, event.block, level, event)
        }
    }

    private inline fun forEachAbility(item: ItemStack, action: (AbilityEnchant, Int) -> Unit) {
        for ((enchant, level) in EnchantData.read(item)) {
            if (enchant is AbilityEnchant) action(enchant, level)
        }
    }

    private fun armorOf(player: Player): List<ItemStack> = listOfNotNull(
        player.inventory.helmet,
        player.inventory.chestplate,
        player.inventory.leggings,
        player.inventory.boots
    )
}
