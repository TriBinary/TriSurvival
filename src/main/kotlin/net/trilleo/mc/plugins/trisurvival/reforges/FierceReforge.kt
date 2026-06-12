package net.trilleo.mc.plugins.trisurvival.reforges

import net.trilleo.mc.plugins.trisurvival.items.ItemRarity
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.stats.Stat

/** Armor reforge granting Strength, Crit Chance and Crit Damage, scaling with rarity. */
object FierceReforge : Reforge("fierce") {

    override val displayName = "Fierce"

    override val applicableTypes = setOf(
        ItemType.HELMET, ItemType.CHESTPLATE, ItemType.LEGGINGS, ItemType.BOOTS
    )

    private val TABLE: Map<ItemRarity, Map<Stat, Double>> = mapOf(
        ItemRarity.COMMON to mapOf(Stat.STRENGTH to 4.0, Stat.CRIT_CHANCE to 1.0, Stat.CRIT_DAMAGE to 4.0),
        ItemRarity.UNCOMMON to mapOf(Stat.STRENGTH to 6.0, Stat.CRIT_CHANCE to 2.0, Stat.CRIT_DAMAGE to 6.0),
        ItemRarity.RARE to mapOf(Stat.STRENGTH to 9.0, Stat.CRIT_CHANCE to 2.0, Stat.CRIT_DAMAGE to 9.0),
        ItemRarity.EPIC to mapOf(Stat.STRENGTH to 13.0, Stat.CRIT_CHANCE to 3.0, Stat.CRIT_DAMAGE to 13.0),
        ItemRarity.LEGENDARY to mapOf(Stat.STRENGTH to 18.0, Stat.CRIT_CHANCE to 4.0, Stat.CRIT_DAMAGE to 18.0),
        ItemRarity.MYTHIC to mapOf(Stat.STRENGTH to 24.0, Stat.CRIT_CHANCE to 5.0, Stat.CRIT_DAMAGE to 24.0),
        ItemRarity.DIVINE to mapOf(Stat.STRENGTH to 31.0, Stat.CRIT_CHANCE to 6.0, Stat.CRIT_DAMAGE to 31.0),
        ItemRarity.SPECIAL to mapOf(Stat.STRENGTH to 39.0, Stat.CRIT_CHANCE to 7.0, Stat.CRIT_DAMAGE to 39.0),
        ItemRarity.VERY_SPECIAL to mapOf(Stat.STRENGTH to 48.0, Stat.CRIT_CHANCE to 8.0, Stat.CRIT_DAMAGE to 48.0),
    )

    override fun statBonuses(rarity: ItemRarity): Map<Stat, Double> = TABLE[rarity] ?: emptyMap()
}
