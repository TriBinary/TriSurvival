package net.trilleo.mc.plugins.trisurvival.reforges

import net.trilleo.mc.plugins.trisurvival.items.ItemRarity
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.stats.Stat

/**
 * A reforge applies a name prefix and rarity-scaled stat bonuses to gear. Concrete
 * reforges are Kotlin `object`s anywhere under the `reforges` package, auto-discovered
 * by [ReforgeRegistry]. A reforge is carried by a [ReforgeStone] and applied at the anvil.
 *
 * [displayName] is the prefix shown on a reforged item's name (e.g. "Heroic"); it is
 * intentionally distinct from the reforge stone's own item name.
 */
abstract class Reforge(val id: String) {

    /** Prefix prepended to a reforged item's name, e.g. "Heroic". */
    abstract val displayName: String

    /** Plugin item types this reforge may be applied to. */
    abstract val applicableTypes: Set<ItemType>

    /** Stat bonuses granted at the given item [rarity]. */
    abstract fun statBonuses(rarity: ItemRarity): Map<Stat, Double>

    fun appliesTo(type: ItemType): Boolean = type in applicableTypes
}
