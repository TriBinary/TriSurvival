package net.trilleo.mc.plugins.trisurvival.utils

import net.trilleo.mc.plugins.trisurvival.items.ItemRarity
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Helpers for reading and changing an item's [ItemRarity], plus the single-use
 * Recombobulator upgrade. [setRarity] only writes the PDC value — callers are
 * responsible for one lore refresh afterwards so name, rarity line and rarity-scaled
 * reforge stats update together.
 */
object RarityUtil {

    /** Recombobulator upgrade path. Deliberately excludes SPECIAL/VERY_SPECIAL. */
    private val UPGRADE_PATH = listOf(
        ItemRarity.COMMON, ItemRarity.UNCOMMON, ItemRarity.RARE, ItemRarity.EPIC,
        ItemRarity.LEGENDARY, ItemRarity.MYTHIC, ItemRarity.DIVINE
    )

    /** The next rarity tier up, or `null` if [rarity] is off-path or already at the cap (DIVINE). */
    fun nextRarityCapped(rarity: ItemRarity): ItemRarity? {
        val index = UPGRADE_PATH.indexOf(rarity)
        return if (index < 0 || index >= UPGRADE_PATH.lastIndex) null else UPGRADE_PATH[index + 1]
    }

    fun readRarity(item: ItemStack): ItemRarity? {
        val name = PDCUtil.get(item, PluginItem.ITEM_RARITY_KEY, PersistentDataType.STRING) ?: return null
        return runCatching { ItemRarity.valueOf(name) }.getOrNull()
    }

    fun setRarity(item: ItemStack, rarity: ItemRarity) {
        PDCUtil.set(item, PluginItem.ITEM_RARITY_KEY, PersistentDataType.STRING, rarity.name)
    }

    fun isRecombobulated(item: ItemStack): Boolean =
        PDCUtil.get(item, PluginItem.RECOMBOBULATED_KEY, PersistentDataType.BOOLEAN) == true

    fun markRecombobulated(item: ItemStack) {
        PDCUtil.set(item, PluginItem.RECOMBOBULATED_KEY, PersistentDataType.BOOLEAN, true)
    }
}
