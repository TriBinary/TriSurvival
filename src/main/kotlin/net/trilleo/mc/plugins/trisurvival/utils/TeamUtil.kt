package net.trilleo.mc.plugins.trisurvival.utils

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.data.ServerDataManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class Team(
    val name: String,
    var displayName: String,
    internal val members: MutableSet<UUID> = mutableSetOf()
) {

    val memberCount: Int get() = members.size

    fun getMembers(): Set<UUID> = members.toSet()

    fun getOnlineMembers(): List<Player> = members.mapNotNull { Bukkit.getPlayer(it) }

    fun contains(player: Player): Boolean = player.uniqueId in members

    fun contains(uuid: UUID): Boolean = uuid in members

    fun broadcast(message: String) {
        val component = MiniMessage.miniMessage().deserialize(message)
        getOnlineMembers().forEach { it.sendMessage(component) }
    }
}

object TeamUtil {

    private const val TEAMS_KEY = "teams"

    private val teamCache = mutableMapOf<String, Team>()
    private val memberIndex = mutableMapOf<UUID, String>()
    private var loaded = false

    fun createTeam(name: String, displayName: String): Boolean {
        ensureLoaded()
        val key = name.lowercase()
        if (teamCache.containsKey(key)) return false
        teamCache[key] = Team(name = key, displayName = displayName)
        persist()
        return true
    }

    fun deleteTeam(name: String): Boolean {
        ensureLoaded()
        val key = name.lowercase()
        val team = teamCache.remove(key) ?: return false
        team.members.forEach { memberIndex.remove(it) }
        persist()
        return true
    }

    fun deleteAll() {
        ensureLoaded()
        teamCache.clear()
        memberIndex.clear()
        persist()
    }

    fun renameTeam(name: String, newDisplayName: String): Boolean {
        ensureLoaded()
        val team = teamCache[name.lowercase()] ?: return false
        team.displayName = newDisplayName
        persist()
        return true
    }

    fun getTeam(name: String): Team? {
        ensureLoaded()
        return teamCache[name.lowercase()]
    }

    fun getAllTeams(): List<Team> {
        ensureLoaded()
        return teamCache.values.toList()
    }

    fun hasTeam(name: String): Boolean {
        ensureLoaded()
        return teamCache.containsKey(name.lowercase())
    }

    fun addPlayer(player: Player, teamName: String): Boolean {
        ensureLoaded()
        val team = teamCache[teamName.lowercase()] ?: return false
        if (team.contains(player)) return false
        val current = teamCache[memberIndex[player.uniqueId]]
        current?.members?.remove(player.uniqueId)
        team.members.add(player.uniqueId)
        memberIndex[player.uniqueId] = team.name
        persist()
        return true
    }

    fun removePlayer(player: Player): Boolean {
        ensureLoaded()
        val team = getPlayerTeam(player) ?: return false
        team.members.remove(player.uniqueId)
        memberIndex.remove(player.uniqueId)
        persist()
        return true
    }

    fun getPlayerTeam(player: Player): Team? {
        ensureLoaded()
        return teamCache[memberIndex[player.uniqueId]]
    }

    fun isInTeam(player: Player, teamName: String): Boolean {
        ensureLoaded()
        return teamCache[teamName.lowercase()]?.contains(player) ?: false
    }

    fun areTeammates(playerA: Player, playerB: Player): Boolean {
        ensureLoaded()
        val teamName = memberIndex[playerA.uniqueId] ?: return false
        return teamName == memberIndex[playerB.uniqueId]
    }

    fun broadcastAll(message: String) {
        ensureLoaded()
        teamCache.values.forEach { it.broadcast(message) }
    }

    fun invalidateCache() {
        loaded = false
        teamCache.clear()
        memberIndex.clear()
    }

    private fun ensureLoaded() {
        if (!loaded) {
            load()
            loaded = true
        }
    }

    private fun load() {
        teamCache.clear()
        memberIndex.clear()
        val teamsArray = ServerDataManager.get().getJsonArray(TEAMS_KEY)
        for (element in teamsArray) {
            if (!element.isJsonObject) continue
            val obj = element.asJsonObject
            val name = obj.get("name")?.asString?.lowercase() ?: continue
            val displayName = obj.get("displayName")?.asString ?: name
            val membersJson = if (obj.has("members") && obj.get("members").isJsonArray)
                obj.getAsJsonArray("members") else JsonArray()
            val members = mutableSetOf<UUID>()
            for (m in membersJson) {
                runCatching {
                    val uuid = UUID.fromString(m.asString)
                    members.add(uuid)
                    memberIndex[uuid] = name
                }
            }
            teamCache[name] = Team(name = name, displayName = displayName, members = members)
        }
    }

    private fun persist() {
        val teamsArray = JsonArray()
        for (team in teamCache.values) {
            val obj = JsonObject()
            obj.addProperty("name", team.name)
            obj.addProperty("displayName", team.displayName)
            val membersArray = JsonArray()
            team.members.forEach { membersArray.add(it.toString()) }
            obj.add("members", membersArray)
            teamsArray.add(obj)
        }
        ServerDataManager.get().set(TEAMS_KEY, teamsArray)
    }
}
