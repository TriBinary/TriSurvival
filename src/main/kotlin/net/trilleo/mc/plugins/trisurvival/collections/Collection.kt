package net.trilleo.mc.plugins.trisurvival.collections

import org.bukkit.Material

/**
 * A single collection — a per-player tally of a gathered resource (e.g. Oak Log, Diamond, Mithril)
 * filed under a [CollectionCategory].
 *
 * Progress is the running total of [sources] obtained. Reaching the cumulative amount in [tiers]
 * advances the player a tier; the list size is the level cap. Tune scaling/cap with [CollectionTiers]
 * or pass an explicit list.
 *
 * Collections are supplied by [CollectionProvider]s, which are auto-discovered anywhere in the
 * `collections` package — exactly like other plugin components.
 */
class Collection(
    val id: String,
    val displayName: String,
    val icon: Material,
    val category: CollectionCategory,
    val sources: List<CollectionSource>,
    val tiers: List<Long>,
    /** When `true`, breaking a player-placed source block does not count (block categories only). */
    val placedFilter: Boolean = false,
    /** Optional rewards keyed by tier number (1-based). */
    val tierRewards: Map<Int, CollectionTierReward> = emptyMap()
) {

    init {
        require(id.matches(Regex("[a-z0-9_]+"))) {
            "Collection id must be lower-case alphanumeric with underscores: $id"
        }
        require(sources.isNotEmpty()) { "Collection $id must declare at least one source" }
        require(tiers.isNotEmpty()) { "Collection $id must define at least one tier" }
    }

    /** The level cap — the number of tiers defined. */
    val maxTier: Int get() = tiers.size

    /** The highest tier whose cumulative threshold is met by [amount] (0 = none reached). */
    fun tierFor(amount: Long): Int {
        var tier = 0
        for (threshold in tiers) {
            if (amount >= threshold) tier++ else break
        }
        return tier
    }

    /** Total amount required for the next tier, or `null` if the max tier is already reached. */
    fun nextThreshold(amount: Long): Long? = tiers.firstOrNull { amount < it }
}
