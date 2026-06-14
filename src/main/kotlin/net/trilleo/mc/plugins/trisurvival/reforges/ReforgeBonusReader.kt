package net.trilleo.mc.plugins.trisurvival.reforges

import net.trilleo.mc.plugins.trisurvival.items.ItemRarity
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.contributors.EquippedItemScanner
import net.trilleo.mc.plugins.trisurvival.utils.PDCUtil
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

/**
 * Aggregates stat bonuses contributed by an item's [Reforge], scaled by the item's
 * rarity. Kept separate from [net.trilleo.mc.plugins.trisurvival.stats.GearBonusReader]
 * so the lore can paint reforge-derived stats in gray, distinct from intrinsic gear
 * stats and enchant stats.
 */
object ReforgeBonusReader {

    /** Stat bonuses from the reforge on a single item, scaled by its rarity. */
    fun reforgeStatBonuses(item: ItemStack): Map<Stat, Double> {
        val reforge = ReforgeData.read(item) ?: return emptyMap()
        val rarity = readRarity(item) ?: return emptyMap()
        return reforge.statBonuses(rarity)
    }

    /** Reforge stat bonuses across the player's equipped slots (same set as gear). */
    fun readEquippedBonuses(player: Player): Map<Stat, Double> {
        val totals = EnumMap<Stat, Double>(Stat::class.java)
        for (equipped in EquippedItemScanner.scan(player)) {
            reforgeStatBonuses(equipped.item).forEach { (stat, value) ->
                totals[stat] = (totals[stat] ?: 0.0) + value
            }
        }
        return totals
    }

    private fun readRarity(item: ItemStack): ItemRarity? {
        val name = PDCUtil.get(item, PluginItem.ITEM_RARITY_KEY, PersistentDataType.STRING) ?: return null
        return runCatching { ItemRarity.valueOf(name) }.getOrNull()
    }
}
