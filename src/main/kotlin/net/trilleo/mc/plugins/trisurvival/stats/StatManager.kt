package net.trilleo.mc.plugins.trisurvival.stats

import net.trilleo.mc.plugins.trisurvival.events.StatRecalcEvent
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

        for (stat in Stat.entries) {
            var value = stat.baseValue
            for (skill in Skill.entries) {
                val level = SkillManager.getLevel(player.uniqueId, skill)
                val bonuses = SkillConfig.getStatBonusesForLevel(skill, level)
                value += bonuses[stat] ?: 0.0
            }
            profile[stat] = value
        }

        if (oldMaxMana > 0) {
            val manaRatio = oldMana / oldMaxMana
            profile.currentMana = manaRatio * profile.maxMana
        } else {
            profile.currentMana = profile.maxMana
        }
        profile.clampMana()

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
        profiles[event.player.uniqueId] = profile
    }

    @EventHandler
    private fun onQuit(event: PlayerQuitEvent) {
        profiles.remove(event.player.uniqueId)
    }

    private fun applyVanillaAttributes(player: Player, profile: StatProfile) {
        // Health: 100 custom HP = 20 vanilla hearts
        val vanillaMaxHealth = (profile.health / 5.0).coerceAtLeast(2.0)
        player.getAttribute(Attribute.MAX_HEALTH)?.let { attr ->
            attr.baseValue = vanillaMaxHealth
        }

        // Speed: 100 custom speed = 0.2 vanilla speed (default walk speed)
        val vanillaSpeed = (profile.speed / 500.0).coerceIn(0.0, 1.0)
        player.walkSpeed = vanillaSpeed.toFloat()
    }
}
