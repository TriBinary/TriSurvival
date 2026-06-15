package net.trilleo.mc.plugins.trisurvival.enchants

import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.contributors.EquippedItemScanner
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * Aggregates stat bonuses contributed by [StatEnchant]s. Kept separate from
 * [net.trilleo.mc.plugins.trisurvival.stats.GearBonusReader] so the lore can
 * paint enchant-derived stats in magenta, distinct from intrinsic gear stats.
 */
object EnchantBonusReader {

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
        for (equipped in EquippedItemScanner.scan(player)) {
            enchantStatBonuses(equipped.item).forEach { (stat, value) ->
                totals[stat] = (totals[stat] ?: 0.0) + value
            }
        }
        return totals
    }
}
