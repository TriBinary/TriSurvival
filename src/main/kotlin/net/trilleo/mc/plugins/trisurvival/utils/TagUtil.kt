package net.trilleo.mc.plugins.trisurvival.utils

import net.trilleo.mc.plugins.trisurvival.data.PlayerData
import net.trilleo.mc.plugins.trisurvival.data.PlayerDataManager
import com.google.gson.JsonArray
import org.bukkit.entity.Player

object TagUtil {

    private const val TAGS_KEY = "tags"

    fun addTag(player: Player, tag: String): Boolean {
        val data = PlayerDataManager.get(player)
        val tags = readTags(data)
        if (!tags.add(tag)) return false
        writeTags(data, tags)
        return true
    }

    fun removeTag(player: Player, tag: String): Boolean {
        val data = PlayerDataManager.get(player)
        val tags = readTags(data)
        if (!tags.remove(tag)) return false
        writeTags(data, tags)
        return true
    }

    fun hasTag(player: Player, tag: String): Boolean =
        tag in readTags(PlayerDataManager.get(player))

    fun getTags(player: Player): Set<String> =
        readTags(PlayerDataManager.get(player)).toSet()

    fun clearTags(player: Player) {
        val data = PlayerDataManager.get(player)
        writeTags(data, mutableSetOf())
    }

    private fun readTags(data: PlayerData): MutableSet<String> {
        val array = data.getJsonArray(TAGS_KEY)
        return array.mapNotNullTo(mutableSetOf()) { element ->
            if (element.isJsonPrimitive) element.asString else null
        }
    }

    private fun writeTags(data: PlayerData, tags: Set<String>) {
        val array = JsonArray()
        tags.forEach { array.add(it) }
        data.set(TAGS_KEY, array)
    }
}
