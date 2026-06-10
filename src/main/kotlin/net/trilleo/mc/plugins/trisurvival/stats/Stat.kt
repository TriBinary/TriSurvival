package net.trilleo.mc.plugins.trisurvival.stats

import net.trilleo.mc.plugins.trisurvival.skills.Skill

enum class Stat(
    val displayName: String,
    val symbol: String,
    val color: String,
    val baseValue: Double,
    val category: StatCategory
) {
    // Combat
    HEALTH("Health", "❤", "<red>", 100.0, StatCategory.HEALTH),
    DEFENSE("Defense", "❈", "<green>", 0.0, StatCategory.COMBAT),
    STRENGTH("Strength", "🗡", "<dark_red>", 0.0, StatCategory.COMBAT),
    CRIT_CHANCE("Crit Chance", "☣", "<blue>", 30.0, StatCategory.COMBAT),
    CRIT_DAMAGE("Crit Damage", "☠", "<blue>", 50.0, StatCategory.COMBAT),
    ATTACK_SPEED("Attack Speed", "⚔", "<yellow>", 0.0, StatCategory.COMBAT),
    FEROCITY("Ferocity", "🔱", "<red>", 0.0, StatCategory.COMBAT),
    SWING_RANGE("Swing Range", "🏹", "<gold>", 0.0, StatCategory.COMBAT),
    SWEEP("Sweep", "⚡", "<dark_purple>", 0.0, StatCategory.COMBAT),

    // Health
    HEALTH_REGEN("Health Regen", "❣", "<light_purple>", 0.0, StatCategory.HEALTH),
    VITALITY("Vitality", "🌿", "<dark_green>", 0.0, StatCategory.HEALTH),
    ABSORPTION("Absorption", "💛", "<gold>", 0.0, StatCategory.HEALTH),

    // Utility
    SPEED("Speed", "✦", "<white>", 100.0, StatCategory.UTILITY),
    INTELLIGENCE("Intelligence", "✎", "<aqua>", 100.0, StatCategory.UTILITY),
    RESPIRATION("Respiration", "🫧", "<aqua>", 0.0, StatCategory.UTILITY),

    // Mining
    MINING_SPEED("Mining Speed", "⛏", "<yellow>", 0.0, StatCategory.MINING),
    MINING_SPREAD("Mining Spread", "◈", "<yellow>", 0.0, StatCategory.MINING),
    MINING_FORTUNE("Mining Fortune", "☘", "<gold>", 0.0, StatCategory.MINING),

    // Farming
    FARMING_FORTUNE("Farming Fortune", "☘", "<gold>", 0.0, StatCategory.FARMING),

    // Foraging
    FORAGING_FORTUNE("Foraging Fortune", "☘", "<gold>", 0.0, StatCategory.FORAGING),

    // Fishing
    FISHING_SPEED("Fishing Speed", "☂", "<aqua>", 0.0, StatCategory.FISHING),
    SEA_CREATURE_CHANCE("Sea Creature Chance", "🦑", "<dark_aqua>", 0.0, StatCategory.FISHING),
    TREASURE_CHANCE("Treasure Chance", "♦", "<gold>", 0.0, StatCategory.FISHING),
    DOUBLE_HOOK_CHANCE("Double Hook", "⚓", "<blue>", 0.0, StatCategory.FISHING),

    // Wisdom (per-skill)
    COMBAT_WISDOM("Combat Wisdom", "☀", "<yellow>", 0.0, StatCategory.UTILITY),
    MINING_WISDOM("Mining Wisdom", "☀", "<yellow>", 0.0, StatCategory.UTILITY),
    FARMING_WISDOM("Farming Wisdom", "☀", "<yellow>", 0.0, StatCategory.UTILITY),
    FORAGING_WISDOM("Foraging Wisdom", "☀", "<yellow>", 0.0, StatCategory.UTILITY),
    FISHING_WISDOM("Fishing Wisdom", "☀", "<yellow>", 0.0, StatCategory.UTILITY),
    ENCHANTING_WISDOM("Enchanting Wisdom", "☀", "<yellow>", 0.0, StatCategory.UTILITY),
    ALCHEMY_WISDOM("Alchemy Wisdom", "☀", "<yellow>", 0.0, StatCategory.UTILITY);

    companion object {
        fun wisdomForSkill(skill: Skill): Stat = when (skill) {
            Skill.COMBAT -> COMBAT_WISDOM
            Skill.MINING -> MINING_WISDOM
            Skill.FARMING -> FARMING_WISDOM
            Skill.FORAGING -> FORAGING_WISDOM
            Skill.FISHING -> FISHING_WISDOM
            Skill.ENCHANTING -> ENCHANTING_WISDOM
            Skill.ALCHEMY -> ALCHEMY_WISDOM
        }
    }
}
