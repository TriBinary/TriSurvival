package net.trilleo.mc.plugins.trisurvival.skills

import net.trilleo.mc.plugins.trisurvival.stats.Stat

data class SkillReward(
    val statBonuses: Map<Stat, Double>,
    val milestones: Map<Int, MilestoneReward> = emptyMap()
)

data class MilestoneReward(
    val description: String,
    val perkId: String? = null
)
