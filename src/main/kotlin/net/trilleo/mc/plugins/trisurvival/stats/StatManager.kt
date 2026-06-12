package net.trilleo.mc.plugins.trisurvival.stats

import net.trilleo.mc.plugins.trisurvival.enchants.EnchantBonusReader
import net.trilleo.mc.plugins.trisurvival.events.StatRecalcEvent
import net.trilleo.mc.plugins.trisurvival.listeners.stats.HealthListener
import net.trilleo.mc.plugins.trisurvival.reforges.ReforgeBonusReader
import net.trilleo.mc.plugins.trisurvival.skills.Skill
import net.trilleo.mc.plugins.trisurvival.skills.SkillConfig
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object StatManager : Listener {

    private val profiles = ConcurrentHashMap<UUID, StatProfile>()
    private lateinit var plugin: JavaPlugin

    private val HEALTH_MODIFIER_KEY = UUID.fromString("a1b2c3d4-0001-0001-0001-000000000001")
    private val SPEED_MODIFIER_KEY = UUID.fromString("a1b2c3d4-0002-0002-0002-000000000002")

    fun init(plugin: JavaPlugin) {
        this.plugin = plugin
        plugin.server.pluginManager.registerEvents(this, plugin)
        plugin.logger.info("StatManager initialised")
    }

    fun recalculate(player: Player): StatProfile {
        val profile = profiles.getOrPut(player.uniqueId) { StatProfile(player.uniqueId) }
        val oldMana = profile.currentMana
        val oldMaxMana = profile.maxMana
        val oldMaxHealth = profile.health
        val oldHealth = profile.currentHealth

        for (stat in Stat.entries) {
            var value = stat.baseValue
            for (skill in Skill.entries) {
                val level = SkillManager.getLevel(player.uniqueId, skill)
                val bonuses = SkillConfig.getStatBonusesForLevel(skill, level)
                value += bonuses[stat] ?: 0.0
            }
            profile[stat] = value
        }

        val gearBonuses = GearBonusReader.readEquippedBonuses(player)
        for ((stat, bonus) in gearBonuses) {
            profile[stat] = profile[stat] + bonus
        }

        val enchantBonuses = EnchantBonusReader.readEquippedEnchantBonuses(player)
        for ((stat, bonus) in enchantBonuses) {
            profile[stat] = profile[stat] + bonus
        }

        val reforgeBonuses = ReforgeBonusReader.readEquippedBonuses(player)
        for ((stat, bonus) in reforgeBonuses) {
            profile[stat] = profile[stat] + bonus
        }

        if (oldMaxMana > 0) {
            val manaRatio = oldMana / oldMaxMana
            profile.currentMana = manaRatio * profile.maxMana
        } else {
            profile.currentMana = profile.maxMana
        }
        profile.clampMana()

        if (oldMaxHealth > 0) {
            val healthRatio = oldHealth / oldMaxHealth
            profile.currentHealth = healthRatio * profile.health
        } else {
            profile.currentHealth = profile.health
        }
        profile.clampHealth()

        applyVanillaAttributes(player, profile)
        Bukkit.getPluginManager().callEvent(StatRecalcEvent(player, profile))
        return profile
    }

    fun getProfile(player: Player): StatProfile =
        profiles.getOrPut(player.uniqueId) { recalculate(player) }

    fun getStat(player: Player, stat: Stat): Double =
        getProfile(player)[stat]

    fun cleanup() {
        profiles.clear()
    }

    @EventHandler
    private fun onJoin(event: PlayerJoinEvent) {
        val profile = StatProfile(event.player.uniqueId)
        profile.currentMana = profile.maxMana
        profile.currentHealth = profile.health
        profiles[event.player.uniqueId] = profile
        // Neutralise vanilla breaking immediately; the full recalc (after async skill load) runs later,
        // and without this the player can mine with vanilla speed in the gap, fighting the engine.
        event.player.getAttribute(Attribute.BLOCK_BREAK_SPEED)?.baseValue = 0.0
    }

    @EventHandler
    private fun onQuit(event: PlayerQuitEvent) {
        profiles.remove(event.player.uniqueId)
    }

    fun syncVanillaHealth(player: Player) {
        val profile = getProfile(player)
        val vanillaMaxHealth = HealthListener.customHealthToHearts(profile.health)
        val vanillaHealth = profile.healthFraction * vanillaMaxHealth
        player.getAttribute(Attribute.MAX_HEALTH)?.baseValue = vanillaMaxHealth
        player.health = vanillaHealth.coerceIn(0.0, vanillaMaxHealth)
    }

    private fun applyVanillaAttributes(player: Player, profile: StatProfile) {
        val vanillaMaxHealth = HealthListener.customHealthToHearts(profile.health)
        player.getAttribute(Attribute.MAX_HEALTH)?.baseValue = vanillaMaxHealth
        player.health = (profile.healthFraction * vanillaMaxHealth).coerceIn(0.0, vanillaMaxHealth)

        // Speed: 100 custom speed = 0.2 vanilla speed (default walk speed)
        val vanillaSpeed = (profile.speed / 500.0).coerceIn(0.0, 1.0)
        player.walkSpeed = vanillaSpeed.toFloat()

        // Attack Speed: vanilla base is 4.0, each point adds 0.04
        player.getAttribute(Attribute.ATTACK_SPEED)?.let { attr ->
            attr.baseValue = 4.0 + (profile[Stat.ATTACK_SPEED] * 0.04)
        }

        // Swing Range: vanilla ENTITY_INTERACTION_RANGE base is 3.0
        player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE)?.let { attr ->
            attr.baseValue = 3.0 + (profile[Stat.SWING_RANGE])
        }

        // Absorption: same scale as health (÷5 for vanilla hearts)
        val absorptionHearts = (profile.absorption / 5.0)
        player.absorptionAmount = absorptionHearts

        // The custom mining engine governs all break timing from MINING_SPEED. Zeroing this attribute
        // (which is synced to the client) stops both client prediction and server-side breaking, so the
        // engine is authoritative without ghost-block desync.
        player.getAttribute(Attribute.BLOCK_BREAK_SPEED)?.baseValue = 0.0

        // Respiration: OXYGEN_BONUS attribute
        player.getAttribute(Attribute.OXYGEN_BONUS)?.let { attr ->
            attr.baseValue = profile[Stat.RESPIRATION].coerceAtLeast(0.0)
        }
    }
}
