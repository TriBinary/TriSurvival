package net.trilleo.mc.plugins.trisurvival.enchants

import net.trilleo.mc.plugins.trisurvival.items.ItemType

/**
 * Base class for all custom enchantments.
 *
 * Concrete enchants are declared as Kotlin `object`s anywhere under the
 * `enchants` package and are auto-discovered by [EnchantRegistry]. Extend
 * [StatEnchant] for stat bonuses or [AbilityEnchant] for active/passive
 * behaviour — never extend this class directly.
 */
abstract class CustomEnchant(val id: String) {

    abstract val displayName: String

    /** Highest level obtainable at all (via book combining). */
    abstract val maxLevel: Int

    /** Highest level obtainable directly at the enchanting table. */
    abstract val maxTableLevel: Int

    /** Plugin item types this enchant may be applied to. */
    abstract val applicableTypes: Set<ItemType>

    /** IDs of enchants that cannot coexist with this one on the same item. */
    open val conflicts: Set<String> = emptySet()

    /** Enchanting skill level required to apply this enchant. */
    abstract val skillRequirement: Int

    /** Vanilla experience levels consumed to apply the given level at the table. */
    abstract fun xpCost(level: Int): Int

    /**
     * Ultimate enchants are book-only (never offered at the table) and an item
     * may hold at most one of them.
     */
    open val ultimate: Boolean = false

    open fun description(level: Int): String = ""

    fun appliesTo(type: ItemType): Boolean = type in applicableTypes

    fun conflictsWith(other: CustomEnchant): Boolean =
        other.id in conflicts || id in other.conflicts
}
