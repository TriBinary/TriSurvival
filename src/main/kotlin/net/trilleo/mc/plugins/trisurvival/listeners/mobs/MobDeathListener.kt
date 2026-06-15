package net.trilleo.mc.plugins.trisurvival.listeners.mobs

import net.trilleo.mc.plugins.trisurvival.events.CustomMobDeathEvent
import net.trilleo.mc.plugins.trisurvival.mobs.runtime.MobManager
import net.trilleo.mc.plugins.trisurvival.skills.Skill
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

/** Resolves custom-mob loot and Combat XP on death, then untracks the instance and its hologram. */
class MobDeathListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onDeath(event: EntityDeathEvent) {
        val instance = MobManager.instanceOf(event.entity) ?: return

        // We cancel vanilla combat damage, so the engine never records a killer — use our tracked one.
        val killer = instance.lastDamager ?: event.entity.killer

        // Vanilla XP orbs are always suppressed — Combat skill XP replaces them. Vanilla ports keep their
        // natural item loot table; bespoke custom mobs drop only their declared loot.
        event.droppedExp = 0
        if (!instance.def.useVanillaDrops) {
            event.drops.clear()
        }
        for (drop in instance.def.drops) {
            event.drops.addAll(drop.roll(instance, killer))
        }

        if (killer != null && instance.def.baseXp > 0.0) {
            SkillManager.addXP(killer, Skill.COMBAT, instance.def.baseXp)
        }

        instance.def.abilities.forEach { it.onDeath(instance, killer) }
        Bukkit.getPluginManager().callEvent(CustomMobDeathEvent(instance.def, event.entity, killer))

        MobManager.untrack(instance)
    }
}
