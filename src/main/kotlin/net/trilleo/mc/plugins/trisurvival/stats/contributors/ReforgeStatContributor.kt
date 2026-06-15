package net.trilleo.mc.plugins.trisurvival.stats.contributors

import net.trilleo.mc.plugins.trisurvival.reforges.ReforgeBonusReader
import net.trilleo.mc.plugins.trisurvival.stats.StatContribution
import net.trilleo.mc.plugins.trisurvival.stats.StatSourceType
import org.bukkit.entity.Player

/** One contribution per equipped item carrying a reforge. */
class ReforgeStatContributor : StatContributor {

    override val order = StatSourceType.REFORGE.order

    override fun contribute(player: Player): List<StatContribution> =
        EquippedItemScanner.scan(player).mapNotNull { equipped ->
            val bonuses = ReforgeBonusReader.reforgeStatBonuses(equipped.item)
            if (bonuses.isEmpty()) return@mapNotNull null
            StatContribution(StatSourceType.REFORGE, equipped.label, bonuses)
        }
}
