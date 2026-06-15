package net.trilleo.mc.plugins.trisurvival.stats.contributors

import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatContribution
import net.trilleo.mc.plugins.trisurvival.stats.StatSourceType
import org.bukkit.entity.Player

/** The intrinsic base value every stat starts from. */
class BaseStatContributor : StatContributor {

    override val order = StatSourceType.BASE.order

    override fun contribute(player: Player): List<StatContribution> {
        val bonuses = Stat.entries.associateWith { it.baseValue }
        return listOf(StatContribution(StatSourceType.BASE, "Base", bonuses))
    }
}
