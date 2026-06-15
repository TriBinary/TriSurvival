package net.trilleo.mc.plugins.trisurvival.stats.contributors

import net.trilleo.mc.plugins.trisurvival.enchants.EnchantBonusReader
import net.trilleo.mc.plugins.trisurvival.stats.StatContribution
import net.trilleo.mc.plugins.trisurvival.stats.StatSourceType
import org.bukkit.entity.Player

/** One contribution per equipped item carrying stat-granting enchantments. */
class EnchantStatContributor : StatContributor {

    override val order = StatSourceType.ENCHANT.order

    override fun contribute(player: Player): List<StatContribution> =
        EquippedItemScanner.scan(player).mapNotNull { equipped ->
            val bonuses = EnchantBonusReader.enchantStatBonuses(equipped.item)
            if (bonuses.isEmpty()) return@mapNotNull null
            StatContribution(StatSourceType.ENCHANT, equipped.label, bonuses)
        }
}
