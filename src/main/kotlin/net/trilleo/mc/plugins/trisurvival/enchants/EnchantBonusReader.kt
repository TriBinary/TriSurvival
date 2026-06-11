package net.trilleo.mc.plugins.trisurvival.enchants

import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.utils.PDCUtil
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

/**
 * Aggregates stat bonuses contributed by [StatEnchant]s. Kept separate from
 * [net.trilleo.mc.plugins.trisurvival.stats.GearBonusReader] so the lore can
 * paint enchant-derived stats in magenta, distinct from intrinsic gear stats.
 */
object EnchantBonusReader {

    private val HELD_TYPES = setOf(
        ItemType.SWORD, ItemType.BOW, ItemType.PICKAXE, ItemType.AXE,
        ItemType.HOE, ItemType.FISHING_ROD
    )

    /** Stat bonuses from all [StatEnchant]s on a single item. */
    fun enchantStatBonuses(item: ItemStack): Map<Stat, Double> {
        val totals = EnumMap<Stat, Double>(Stat::class.java)
        for ((enchant, level) in EnchantData.read(item)) {
            if (enchant !is StatEnchant) continue
            enchant.statBonuses(level).forEach { (stat, value) ->
                totals[stat] = (totals[stat] ?: 0.0) + value
            }
        }
        return totals
    }

    /** Enchant stat bonuses across the player's equipped slots (same set as gear). */
    fun readEquippedEnchantBonuses(player: Player): Map<Stat, Double> {
        val totals = EnumMap<Stat, Double>(Stat::class.java)
        val inv = player.inventory

        fun add(item: ItemStack) {
            enchantStatBonuses(item).forEach { (stat, value) ->
                totals[stat] = (totals[stat] ?: 0.0) + value
            }
        }

        for (armor in listOfNotNull(inv.helmet, inv.chestplate, inv.leggings, inv.boots)) {
            add(armor)
        }

        val mainHand = inv.itemInMainHand.takeIf { !it.type.isAir }
        val offHand = inv.itemInOffHand.takeIf { !it.type.isAir }
        for (held in listOfNotNull(mainHand, offHand)) {
            val type = getItemType(held)
            if (type in HELD_TYPES || type == ItemType.NONE) add(held)
        }

        for (i in 0 until inv.size) {
            val item = inv.getItem(i) ?: continue
            if (item.type.isAir) continue
            if (getItemType(item) == ItemType.ACCESSORY) add(item)
        }

        return totals
    }

    private fun getItemType(item: ItemStack): ItemType {
        val name = PDCUtil.get(item, PluginItem.ITEM_TYPE_KEY, PersistentDataType.STRING)
            ?: return ItemType.NONE
        return runCatching { ItemType.valueOf(name) }.getOrDefault(ItemType.NONE)
    }
}
