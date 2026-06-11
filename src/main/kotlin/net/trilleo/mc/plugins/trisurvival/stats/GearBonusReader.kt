package net.trilleo.mc.plugins.trisurvival.stats

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
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

    private val ARMOR_TYPES = setOf(
        ItemType.HELMET, ItemType.CHESTPLATE, ItemType.LEGGINGS, ItemType.BOOTS
    )

    private val HELD_TYPES = setOf(
        ItemType.SWORD, ItemType.BOW, ItemType.PICKAXE, ItemType.AXE,
        ItemType.HOE, ItemType.FISHING_ROD
    )

    fun readEquippedBonuses(player: Player): Map<Stat, Double> {
        val totals = EnumMap<Stat, Double>(Stat::class.java)
        val inv = player.inventory

        fun addBonuses(item: ItemStack) {
            parseBonuses(item).forEach { (stat, value) ->
                totals[stat] = (totals[stat] ?: 0.0) + value
            }
        }

        for (armorItem in listOfNotNull(inv.helmet, inv.chestplate, inv.leggings, inv.boots)) {
            addBonuses(armorItem)
        }

        val mainHand = inv.itemInMainHand.takeIf { !it.type.isAir }
        val offHand = inv.itemInOffHand.takeIf { !it.type.isAir }

        for (heldItem in listOfNotNull(mainHand, offHand)) {
            val itemType = getItemType(heldItem)
            if (itemType in HELD_TYPES || itemType == ItemType.NONE) {
                addBonuses(heldItem)
            }
        }

        for (i in 0 until inv.size) {
            val item = inv.getItem(i) ?: continue
            if (item.type.isAir) continue
            val itemType = getItemType(item)
            if (itemType == ItemType.ACCESSORY) {
                addBonuses(item)
            }
        }

        return totals
    }

    private fun getItemType(item: ItemStack): ItemType {
        val typeName = PDCUtil.get(item, PluginItem.ITEM_TYPE_KEY, PersistentDataType.STRING)
            ?: return ItemType.NONE
        return runCatching { ItemType.valueOf(typeName) }.getOrDefault(ItemType.NONE)
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
