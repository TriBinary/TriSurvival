package net.trilleo.mc.plugins.trisurvival.listeners.skills

import net.trilleo.mc.plugins.trisurvival.skills.Skill
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent

class EnchantingXPListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onEnchant(event: EnchantItemEvent) {
        val xp = event.expLevelCost * 5.0
        SkillManager.addXP(event.enchanter, Skill.ENCHANTING, xp)
    }
}
