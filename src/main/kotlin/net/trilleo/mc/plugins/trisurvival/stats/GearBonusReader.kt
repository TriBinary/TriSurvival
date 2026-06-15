package net.trilleo.mc.plugins.trisurvival.stats

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.trilleo.mc.plugins.trisurvival.stats.contributors.EquippedItemScanner
import net.trilleo.mc.plugins.trisurvival.utils.PDCUtil
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

object GearBonusReader {

    @JvmField
    val STAT_BONUSES_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:stat_bonuses")!!

    private val gson = Gson()
    private val mapType = object : TypeToken<Map<String, Double>>() {}.type

    fun readEquippedBonuses(player: Player): Map<Stat, Double> {
        val totals = EnumMap<Stat, Double>(Stat::class.java)
        for (equipped in EquippedItemScanner.scan(player)) {
            parseBonuses(equipped.item).forEach { (stat, value) ->
                totals[stat] = (totals[stat] ?: 0.0) + value
            }
        }
        return totals
    }

    fun parseBonuses(item: ItemStack): Map<Stat, Double> {
        val json = PDCUtil.get(item, STAT_BONUSES_KEY, PersistentDataType.STRING) ?: return emptyMap()
        return try {
            val raw: Map<String, Double> = gson.fromJson(json, mapType)
            raw.mapNotNull { (name, value) ->
                val stat = try {
                    Stat.valueOf(name)
                } catch (_: IllegalArgumentException) {
                    null
                }
                stat?.let { it to value }
            }.toMap()
        } catch (_: Exception) {
            emptyMap()
        }
    }

    fun encodeBonuses(bonuses: Map<Stat, Double>): String {
        val raw = bonuses.mapKeys { (stat, _) -> stat.name }
        return gson.toJson(raw)
    }
}
