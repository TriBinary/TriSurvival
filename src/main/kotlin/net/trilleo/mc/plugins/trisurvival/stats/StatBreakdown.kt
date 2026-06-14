package net.trilleo.mc.plugins.trisurvival.stats

/**
 * The captured per-source breakdown for a [StatProfile], produced during recalculation. Because the same
 * list of [contributions] is summed into the profile, the breakdown is guaranteed to add up to the
 * displayed totals.
 */
class StatBreakdown(val contributions: List<StatContribution>) {

    /** Contributions that add a non-zero amount to [stat], in their original (source-ordered) order. */
    fun forStat(stat: Stat): List<StatContribution> =
        contributions.filter { (it.bonuses[stat] ?: 0.0) != 0.0 }

    /** Contributions affecting [stat], grouped by source type and ordered by [StatSourceType.order]. */
    fun byTypeForStat(stat: Stat): Map<StatSourceType, List<StatContribution>> =
        forStat(stat)
            .groupBy { it.type }
            .toSortedMap(compareBy { it.order })
}
