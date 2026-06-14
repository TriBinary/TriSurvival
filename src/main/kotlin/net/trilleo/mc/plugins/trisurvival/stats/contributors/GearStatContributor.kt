package net.trilleo.mc.plugins.trisurvival.stats.contributors

import net.trilleo.mc.plugins.trisurvival.stats.GearBonusReader
import net.trilleo.mc.plugins.trisurvival.stats.StatContribution
import net.trilleo.mc.plugins.trisurvival.stats.StatSourceType
import org.bukkit.entity.Player

/** One contribution per equipped item carrying intrinsic gear stat bonuses. */
class GearStatContributor : StatContributor {

    override val order = StatSourceType.GEAR.order

    override fun contribute(player: Player): List<StatContribution> =
        EquippedItemScanner.scan(player).mapNotNull { equipped ->
            val bonuses = GearBonusReader.parseBonuses(equipped.item)
            if (bonuses.isEmpty()) return@mapNotNull null
            StatContribution(StatSourceType.GEAR, equipped.label, bonuses)
        }
}
