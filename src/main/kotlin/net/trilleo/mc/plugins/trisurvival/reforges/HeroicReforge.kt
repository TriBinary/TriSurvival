package net.trilleo.mc.plugins.trisurvival.reforges

import net.trilleo.mc.plugins.trisurvival.items.ItemRarity
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.stats.Stat

/** Weapon reforge granting Strength and Crit Damage, scaling with rarity. */
object HeroicReforge : Reforge("heroic") {

    override val displayName = "Heroic"

    override val applicableTypes = setOf(ItemType.SWORD, ItemType.BOW)

    private val TABLE: Map<ItemRarity, Map<Stat, Double>> = mapOf(
        ItemRarity.COMMON to mapOf(Stat.STRENGTH to 5.0, Stat.CRIT_DAMAGE to 8.0),
        ItemRarity.UNCOMMON to mapOf(Stat.STRENGTH to 8.0, Stat.CRIT_DAMAGE to 13.0),
        ItemRarity.RARE to mapOf(Stat.STRENGTH to 12.0, Stat.CRIT_DAMAGE to 20.0),
        ItemRarity.EPIC to mapOf(Stat.STRENGTH to 17.0, Stat.CRIT_DAMAGE to 29.0),
        ItemRarity.LEGENDARY to mapOf(Stat.STRENGTH to 23.0, Stat.CRIT_DAMAGE to 40.0),
        ItemRarity.MYTHIC to mapOf(Stat.STRENGTH to 30.0, Stat.CRIT_DAMAGE to 53.0),
        ItemRarity.DIVINE to mapOf(Stat.STRENGTH to 38.0, Stat.CRIT_DAMAGE to 68.0),
        ItemRarity.SPECIAL to mapOf(Stat.STRENGTH to 47.0, Stat.CRIT_DAMAGE to 85.0),
        ItemRarity.VERY_SPECIAL to mapOf(Stat.STRENGTH to 57.0, Stat.CRIT_DAMAGE to 104.0),
    )

    override fun statBonuses(rarity: ItemRarity): Map<Stat, Double> = TABLE[rarity] ?: emptyMap()
}
