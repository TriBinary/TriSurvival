package net.trilleo.mc.plugins.trisurvival.enchants.ability

import net.trilleo.mc.plugins.trisurvival.enchants.AbilityEnchant
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent

object Thunderlord : AbilityEnchant("thunderlord") {
    override val displayName = "Thunderlord"
    override val maxLevel = 6
    override val maxTableLevel = 5
    override val applicableTypes = setOf(ItemType.SWORD, ItemType.AXE)
    override val skillRequirement = 10

    override fun xpCost(level: Int): Int = level * 4

    override fun onAttack(player: Player, victim: LivingEntity, level: Int, event: EntityDamageByEntityEvent) {
        victim.world.strikeLightningEffect(victim.location)
        event.damage += event.damage * 0.05 * level
    }

    override fun description(level: Int): String =
        "On hit, strike lightning dealing <yellow>+${5 * level}%<gray> bonus damage."
}
