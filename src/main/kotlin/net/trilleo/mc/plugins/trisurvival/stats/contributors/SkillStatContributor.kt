package net.trilleo.mc.plugins.trisurvival.stats.contributors

import net.trilleo.mc.plugins.trisurvival.skills.Skill
import net.trilleo.mc.plugins.trisurvival.skills.SkillConfig
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
import net.trilleo.mc.plugins.trisurvival.stats.StatContribution
import net.trilleo.mc.plugins.trisurvival.stats.StatSourceType
import org.bukkit.entity.Player

/** One contribution per skill, scaled by the player's level in that skill. */
class SkillStatContributor : StatContributor {

    override val order = StatSourceType.SKILL.order

    override fun contribute(player: Player): List<StatContribution> =
        Skill.entries.mapNotNull { skill ->
            val level = SkillManager.getLevel(player.uniqueId, skill)
            if (level <= 0) return@mapNotNull null
            val bonuses = SkillConfig.getStatBonusesForLevel(skill, level)
            if (bonuses.isEmpty()) return@mapNotNull null
            StatContribution(StatSourceType.SKILL, "${skill.displayName} — Level $level", bonuses)
        }
}
