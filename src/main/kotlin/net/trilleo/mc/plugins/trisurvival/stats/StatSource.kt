package net.trilleo.mc.plugins.trisurvival.stats

import net.trilleo.mc.plugins.trisurvival.skills.Skill

sealed class StatSource(val priority: Int) {

    data object Base : StatSource(0)

    data class SkillBonus(val skill: Skill, val level: Int) : StatSource(1)

    data class GearBonus(val slotName: String) : StatSource(2)

    data class Buff(val id: String, val expiresAt: Long) : StatSource(3)
}
