package net.trilleo.mc.plugins.trisurvival.enchants

import net.trilleo.mc.plugins.trisurvival.stats.Stat

/**
 * An enchant whose bonus is a set of plugin stats scaling with level. The
 * resulting stats are folded into the player's [net.trilleo.mc.plugins.trisurvival.stats.StatProfile]
 * by [EnchantBonusReader] and shown in magenta on the item's stat lines.
 */
abstract class StatEnchant(id: String) : CustomEnchant(id) {

    abstract fun statBonuses(level: Int): Map<Stat, Double>
}
