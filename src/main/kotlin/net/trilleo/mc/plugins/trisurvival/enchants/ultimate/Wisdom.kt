package net.trilleo.mc.plugins.trisurvival.enchants.ultimate

import net.trilleo.mc.plugins.trisurvival.enchants.AbilityEnchant
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.skills.Skill
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent

object Wisdom : AbilityEnchant("wisdom") {
    override val displayName = "Wisdom"
    override val maxLevel = 5
    override val maxTableLevel = 0
    override val applicableTypes = setOf(ItemType.SWORD, ItemType.AXE)
    override val skillRequirement = 20
    override val ultimate = true

    override fun xpCost(level: Int): Int = level * 10

    override fun onAttack(player: Player, victim: LivingEntity, level: Int, event: EntityDamageByEntityEvent) {
        SkillManager.addXP(player, Skill.COMBAT, level.toDouble())
    }

    override fun description(level: Int): String =
        "Gain <yellow>+${level} bonus Combat XP<gray> on every hit."
}
