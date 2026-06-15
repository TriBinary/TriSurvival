package net.trilleo.mc.plugins.trisurvival.skills

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import net.trilleo.mc.plugins.trisurvival.data.SkillDAO
import net.trilleo.mc.plugins.trisurvival.events.SkillLevelUpEvent
import net.trilleo.mc.plugins.trisurvival.events.SkillXPGainEvent
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object SkillManager : Listener {

    private val mm = MiniMessage.miniMessage()
    private lateinit var plugin: JavaPlugin

    private val playerSkills = ConcurrentHashMap<UUID, EnumMap<Skill, SkillState>>()
    private val dirtyPlayers = ConcurrentHashMap.newKeySet<UUID>()
    private val actionBarTasks = ConcurrentHashMap<UUID, BukkitTask>()
    private val xpActionBarActive = ConcurrentHashMap.newKeySet<UUID>()

    fun hasActiveXPBar(uuid: UUID): Boolean = uuid in xpActionBarActive

    data class SkillState(var xp: Double = 0.0, var level: Int = 0)

    fun init(plugin: JavaPlugin) {
        this.plugin = plugin
        plugin.server.pluginManager.registerEvents(this, plugin)

        // Load data for any players already online (e.g., plugin reload)
        plugin.server.onlinePlayers.forEach { loadPlayer(it) }

        plugin.logger.info("SkillManager initialised")
    }

    fun addXP(player: Player, skill: Skill, amount: Double) {
        val state = getState(player.uniqueId, skill)
        if (state.level >= skill.maxLevel) return

        val event = SkillXPGainEvent(player, skill, amount)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) return

        state.xp += event.amount
        dirtyPlayers.add(player.uniqueId)

        while (state.level < skill.maxLevel) {
            val xpNeeded = skill.totalXpForLevel(state.level + 1)
            if (state.xp < xpNeeded) break

            val oldLevel = state.level
            state.level++
            Bukkit.getPluginManager().callEvent(SkillLevelUpEvent(player, skill, oldLevel, state.level))
            sendLevelUpMessage(player, skill, state.level)
            StatManager.recalculate(player)
        }

        sendXPGainActionBar(player, skill, event.amount)
    }

    fun getLevel(uuid: UUID, skill: Skill): Int = getState(uuid, skill).level

    fun getXP(uuid: UUID, skill: Skill): Double = getState(uuid, skill).xp

    fun getProgress(uuid: UUID, skill: Skill): Double {
        val state = getState(uuid, skill)
        if (state.level >= skill.maxLevel) return 1.0
        val currentLevelXP = if (state.level == 0) 0.0 else skill.totalXpForLevel(state.level)
        val nextLevelXP = skill.totalXpForLevel(state.level + 1)
        val range = nextLevelXP - currentLevelXP
        if (range <= 0) return 1.0
        return ((state.xp - currentLevelXP) / range).coerceIn(0.0, 1.0)
    }

    fun saveAll() {
        for (uuid in playerSkills.keys) {
            savePlayer(uuid)
        }
        dirtyPlayers.clear()
    }

    fun saveDirty() {
        val snapshot = dirtyPlayers.toSet()
        dirtyPlayers.removeAll(snapshot)
        for (uuid in snapshot) {
            savePlayer(uuid)
        }
    }

    // ── Internal ────────────────────────────────────────────────────────

    @EventHandler
    private fun onJoin(event: PlayerJoinEvent) {
        loadPlayer(event.player)
    }

    @EventHandler
    private fun onQuit(event: PlayerQuitEvent) {
        val uuid = event.player.uniqueId
        savePlayer(uuid)
        playerSkills.remove(uuid)
        dirtyPlayers.remove(uuid)
        actionBarTasks.remove(uuid)?.cancel()
        xpActionBarActive.remove(uuid)
    }

    private fun loadPlayer(player: Player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val data = SkillDAO.loadAll(player.uniqueId)
            val skills = EnumMap<Skill, SkillState>(Skill::class.java)
            Skill.entries.forEach { skill ->
                val pair = data[skill]
                skills[skill] = if (pair != null) SkillState(pair.first, pair.second) else SkillState()
            }
            Bukkit.getScheduler().runTask(plugin, Runnable {
                if (player.isOnline) {
                    playerSkills[player.uniqueId] = skills
                    StatManager.recalculate(player)
                }
            })
        })
    }

    private fun savePlayer(uuid: UUID) {
        val skills = playerSkills[uuid] ?: return
        val data = skills.mapValues { (_, state) -> Pair(state.xp, state.level) }
        // During onDisable the scheduler rejects new tasks, so write on the current thread instead.
        if (!plugin.isEnabled) {
            SkillDAO.saveAll(uuid, data)
            return
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            SkillDAO.saveAll(uuid, data)
        })
    }

    private fun getState(uuid: UUID, skill: Skill): SkillState {
        val skills = playerSkills.getOrPut(uuid) {
            val map = EnumMap<Skill, SkillState>(Skill::class.java)
            Skill.entries.forEach { map[it] = SkillState() }
            map
        }
        return skills.getOrPut(skill) { SkillState() }
    }

    private fun sendLevelUpMessage(player: Player, skill: Skill, newLevel: Int) {
        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)

        player.showTitle(
            Title.title(
                mm.deserialize("<gold><bold>SKILL LEVEL UP"),
                mm.deserialize("<yellow>${skill.displayName} <gold>${newLevel}")
            )
        )

        player.sendMessage(Component.empty())
        player.sendMessage(
            mm.deserialize("  <gold><bold>SKILL LEVEL UP <yellow>${skill.displayName} <dark_gray>(<yellow>${newLevel - 1}<dark_gray> → <yellow>${newLevel}<dark_gray>)")
        )

        val reward = SkillConfig.getReward(skill)
        for ((stat, bonus) in reward.statBonuses) {
            player.sendMessage(
                mm.deserialize("    ${stat.color}${stat.symbol} ${stat.displayName} ${stat.color}+${formatNumber(bonus)}")
            )
        }

        val milestone = SkillConfig.getMilestone(skill, newLevel)
        if (milestone != null) {
            player.playSound(player.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f)
            player.sendMessage(
                mm.deserialize("    <light_purple><bold>NEW MILESTONE: <light_purple>${milestone.description}")
            )
        }

        player.sendMessage(Component.empty())
    }

    private fun sendXPGainActionBar(player: Player, skill: Skill, amount: Double) {
        val uuid = player.uniqueId
        val message = mm.deserialize(
            "<aqua>+${formatNumber(amount)} ${skill.displayName} XP <dark_gray>(${
                formatNumber(getProgress(uuid, skill) * 100)
            }%)"
        )

        actionBarTasks.remove(uuid)?.cancel()
        xpActionBarActive.add(uuid)

        player.sendActionBar(message)

        var ticks = 0
        val task = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            ticks += 20
            if (ticks > 60 || !player.isOnline) {
                actionBarTasks.remove(uuid)?.cancel()
                xpActionBarActive.remove(uuid)
                return@Runnable
            }
            player.sendActionBar(message)
        }, 20L, 20L)
        actionBarTasks[uuid] = task
    }

    private fun formatNumber(value: Double): String =
        if (value == value.toLong().toDouble()) value.toLong().toString()
        else String.format("%.1f", value)
}
