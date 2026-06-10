package net.trilleo.mc.plugins.trisurvival.skills

import net.trilleo.mc.plugins.trisurvival.stats.Stat

object SkillConfig {

    private val rewards = mapOf(
        Skill.COMBAT to SkillReward(
            statBonuses = mapOf(
                Stat.STRENGTH to 1.0,
                Stat.CRIT_CHANCE to 0.5,
                Stat.FEROCITY to 0.2,
                Stat.ATTACK_SPEED to 0.3,
                Stat.COMBAT_WISDOM to 0.1
            ),
            milestones = mapOf(
                10 to MilestoneReward("First Strike: +5% base weapon damage"),
                25 to MilestoneReward("Warrior's Fury: Critical hits deal extra knockback"),
                50 to MilestoneReward("Berserker: Gain Strength boost when below 25% HP")
            )
        ),
        Skill.MINING to SkillReward(
            statBonuses = mapOf(
                Stat.DEFENSE to 1.0,
                Stat.STRENGTH to 0.5,
                Stat.MINING_SPEED to 0.5,
                Stat.MINING_FORTUNE to 0.3,
                Stat.MINING_SPREAD to 0.2,
                Stat.MINING_WISDOM to 0.1
            ),
            milestones = mapOf(
                10 to MilestoneReward("Efficient Miner: Small chance to double ore drops"),
                25 to MilestoneReward("Experienced Miner: Haste I while mining"),
                50 to MilestoneReward("Master Miner: Chance to find rare minerals")
            )
        ),
        Skill.FARMING to SkillReward(
            statBonuses = mapOf(
                Stat.HEALTH to 2.0,
                Stat.FARMING_FORTUNE to 0.4,
                Stat.FARMING_WISDOM to 0.1
            ),
            milestones = mapOf(
                10 to MilestoneReward("Green Thumb: Small chance for double crop drops"),
                25 to MilestoneReward("Harvester: Crops in a small radius auto-replant"),
                50 to MilestoneReward("Master Farmer: Greatly increased crop yield")
            )
        ),
        Skill.FORAGING to SkillReward(
            statBonuses = mapOf(
                Stat.STRENGTH to 1.0,
                Stat.SWEEP to 0.1,
                Stat.FORAGING_FORTUNE to 0.3,
                Stat.FORAGING_WISDOM to 0.1
            ),
            milestones = mapOf(
                10 to MilestoneReward("Lumberjack: Small chance for double log drops"),
                25 to MilestoneReward("Evolved Axe: Increased axe damage"),
                50 to MilestoneReward("Master Forager: Entire trees fall at once")
            )
        ),
        Skill.FISHING to SkillReward(
            statBonuses = mapOf(
                Stat.HEALTH to 1.0,
                Stat.INTELLIGENCE to 1.0,
                Stat.FISHING_SPEED to 0.5,
                Stat.SEA_CREATURE_CHANCE to 0.1,
                Stat.TREASURE_CHANCE to 0.15,
                Stat.FISHING_WISDOM to 0.1
            ),
            milestones = mapOf(
                10 to MilestoneReward("Angler: Reduced fishing wait time"),
                25 to MilestoneReward("Treasure Hunter: Higher chance of treasure"),
                50 to MilestoneReward("Master Angler: Chance to catch rare sea creatures")
            )
        ),
        Skill.ENCHANTING to SkillReward(
            statBonuses = mapOf(
                Stat.INTELLIGENCE to 2.0,
                Stat.ENCHANTING_WISDOM to 0.1
            ),
            milestones = mapOf(
                10 to MilestoneReward("Scholarly: +5% enchanting XP"),
                25 to MilestoneReward("Wise: Reduced anvil costs"),
                50 to MilestoneReward("Arcane Master: Chance to apply bonus enchantment")
            )
        ),
        Skill.ALCHEMY to SkillReward(
            statBonuses = mapOf(
                Stat.INTELLIGENCE to 1.5,
                Stat.HEALTH to 1.0,
                Stat.ALCHEMY_WISDOM to 0.1
            ),
            milestones = mapOf(
                10 to MilestoneReward("Brewer: Potions last 10% longer"),
                25 to MilestoneReward("Alchemist: Chance to brew extra potion"),
                50 to MilestoneReward("Master Alchemist: Potions are 25% more potent")
            )
        )
    )

    fun getReward(skill: Skill): SkillReward =
        rewards[skill] ?: SkillReward(emptyMap())

    fun getStatBonusesForLevel(skill: Skill, level: Int): Map<Stat, Double> {
        val reward = getReward(skill)
        return reward.statBonuses.mapValues { (_, bonus) -> bonus * level }
    }

    fun getMilestone(skill: Skill, level: Int): MilestoneReward? =
        getReward(skill).milestones[level]
}
