package net.trilleo.mc.plugins.trisurvival.listeners.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.inventory.PrepareGrindstoneEvent
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta

/**
 * Removes vanilla enchanting from the game. The table, anvil, and grindstone are
 * replaced by custom GUIs (those interactions are cancelled elsewhere); this
 * listener defensively cancels the vanilla enchant events and strips vanilla
 * enchantments from every other source (loot, mob gear, trades, pickups).
 */
class VanillaEnchantBlocker : Listener {

    @EventHandler
    fun onEnchant(event: EnchantItemEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onPrepareEnchant(event: PrepareItemEnchantEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onPrepareAnvil(event: PrepareAnvilEvent) {
        event.result = null
    }

    @EventHandler
    fun onPrepareGrindstone(event: PrepareGrindstoneEvent) {
        event.result = null
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onLoot(event: LootGenerateEvent) {
        for (item in event.loot) {
            stripVanillaEnchants(item)
        }
    }

    @EventHandler
    fun onSpawn(event: CreatureSpawnEvent) {
        val equipment = event.entity.equipment ?: return
        for (item in listOf(
            equipment.helmet, equipment.chestplate, equipment.leggings, equipment.boots,
            equipment.itemInMainHand, equipment.itemInOffHand
        )) {
            stripVanillaEnchants(item)
        }
    }

    @EventHandler
    fun onTrade(event: VillagerAcquireTradeEvent) {
        val result = event.recipe.result
        if (result.enchantments.isNotEmpty() || result.itemMeta is EnchantmentStorageMeta) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPickup(event: EntityPickupItemEvent) {
        stripVanillaEnchants(event.item.itemStack)
    }

    private fun stripVanillaEnchants(item: ItemStack) {
        if (item.enchantments.isNotEmpty()) {
            item.enchantments.keys.toList().forEach { item.removeEnchantment(it) }
        }
        val meta = item.itemMeta
        if (meta is EnchantmentStorageMeta && meta.hasStoredEnchants()) {
            meta.storedEnchants.keys.toList().forEach { meta.removeStoredEnchant(it) }
            item.itemMeta = meta
        }
    }
}
