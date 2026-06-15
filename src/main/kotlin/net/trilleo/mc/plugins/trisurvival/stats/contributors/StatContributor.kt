package net.trilleo.mc.plugins.trisurvival.stats.contributors

import net.trilleo.mc.plugins.trisurvival.stats.StatContribution
import org.bukkit.entity.Player

/**
 * Supplies the stat bonuses from one source. Contributors are summed by
 * [net.trilleo.mc.plugins.trisurvival.stats.StatManager] to build a player's stats and, in the same pass,
 * captured as the per-source breakdown shown in the stats GUI. Adding a new source of stats means writing
 * one of these and registering it with [StatContributorRegistry].
 */
interface StatContributor {

    /** Determines evaluation order; lower runs first. Has no gameplay effect (addition is commutative). */
    val order: Int

    /** The fine-grained contributions this source provides for [player]; may be empty. */
    fun contribute(player: Player): List<StatContribution>
}
