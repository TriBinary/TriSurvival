package net.trilleo.mc.plugins.trisurvival.listeners.enchants

import net.trilleo.mc.plugins.trisurvival.enchants.CustomEnchant
import net.trilleo.mc.plugins.trisurvival.enchants.EnchantBook
import net.trilleo.mc.plugins.trisurvival.enchants.stats.Growth
import net.trilleo.mc.plugins.trisurvival.enchants.stats.Protection
import net.trilleo.mc.plugins.trisurvival.enchants.stats.Sharpness
import org.bukkit.entity.Monster
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

/**
 * Example loot source: hostile mobs have a small chance to drop a level-1
 * enchant book. Mirror this pattern (or a BlockBreakEvent variant for ores) to
 * add more drop sources.
 */
class EnchantBookDropListener : Listener {

    private val pool: List<CustomEnchant> = listOf(Sharpness, Protection, Growth)
    private val dropChance = 0.02

    @EventHandler
    fun onDeath(event: EntityDeathEvent) {
        if (event.entity !is Monster) return
        if (Math.random() >= dropChance) return
        val enchant = pool.random()
        event.drops.add(EnchantBook.create(enchant, 1))
    }
}
