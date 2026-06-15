package net.trilleo.mc.plugins.trisurvival.listeners.skills

import net.trilleo.mc.plugins.trisurvival.mobs.CustomMob
import net.trilleo.mc.plugins.trisurvival.skills.Skill
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class CombatXPListener : Listener {

    private val xpValues = mapOf(
        EntityType.ZOMBIE to 10.0,
        EntityType.SKELETON to 12.0,
        EntityType.SPIDER to 10.0,
        EntityType.CREEPER to 15.0,
        EntityType.ENDERMAN to 20.0,
        EntityType.WITCH to 18.0,
        EntityType.BLAZE to 20.0,
        EntityType.GHAST to 25.0,
        EntityType.PIGLIN_BRUTE to 22.0,
        EntityType.WITHER_SKELETON to 25.0,
        EntityType.PHANTOM to 15.0,
        EntityType.DROWNED to 12.0,
        EntityType.HUSK to 12.0,
        EntityType.STRAY to 12.0,
        EntityType.CAVE_SPIDER to 14.0,
        EntityType.SILVERFISH to 5.0,
        EntityType.SLIME to 8.0,
        EntityType.MAGMA_CUBE to 12.0,
        EntityType.GUARDIAN to 18.0,
        EntityType.ELDER_GUARDIAN to 50.0,
        EntityType.WARDEN to 100.0,
        EntityType.WITHER to 250.0,
        EntityType.ENDER_DRAGON to 500.0
    )

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        // Custom mobs grant Combat XP via their own baseXp in MobDeathListener.
        if (CustomMob.isCustom(event.entity)) return
        val player = event.entity.killer ?: return
        val xp = xpValues[event.entity.type] ?: 5.0
        SkillManager.addXP(player, Skill.COMBAT, xp)
    }
}
