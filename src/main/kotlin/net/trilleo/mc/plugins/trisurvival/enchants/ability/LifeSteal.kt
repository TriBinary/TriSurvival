package net.trilleo.mc.plugins.trisurvival.enchants.ability

import net.trilleo.mc.plugins.trisurvival.enchants.AbilityEnchant
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent

object LifeSteal : AbilityEnchant("life_steal") {
    override val displayName = "Life Steal"
    override val maxLevel = 5
    override val maxTableLevel = 3
    override val applicableTypes = setOf(ItemType.SWORD)
    override val skillRequirement = 8

    override fun xpCost(level: Int): Int = level * 4

    override fun onAttack(player: Player, victim: LivingEntity, level: Int, event: EntityDamageByEntityEvent) {
        val maxHealth = player.getAttribute(Attribute.MAX_HEALTH)?.value ?: return
        val heal = event.damage * 0.02 * level
        player.health = (player.health + heal).coerceAtMost(maxHealth)
    }

    override fun description(level: Int): String =
        "Heals you for <red>${2 * level}%<gray> of the damage you deal."
}
