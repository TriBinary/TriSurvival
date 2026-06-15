package net.trilleo.mc.plugins.trisurvival.stats

/**
 * A single fine-grained stat contribution from one sub-source. [label] names the concrete origin within
 * its [type] — e.g. `"Combat — Level 12"` for a [StatSourceType.SKILL], or an item's display name for
 * [StatSourceType.GEAR]. [bonuses] maps each affected [Stat] to the amount this sub-source adds.
 */
data class StatContribution(
    val type: StatSourceType,
    val label: String,
    val bonuses: Map<Stat, Double>
)
