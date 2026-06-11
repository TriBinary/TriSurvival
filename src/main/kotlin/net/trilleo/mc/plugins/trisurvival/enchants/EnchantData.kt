package net.trilleo.mc.plugins.trisurvival.enchants

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.trilleo.mc.plugins.trisurvival.items.ItemLoreGenerator
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Reads and writes the custom enchants stored on an item. Enchants live in the
 * item PDC under [ENCHANTS_KEY] as a JSON `{enchantId: level}` map, mirroring the
 * approach used by [net.trilleo.mc.plugins.trisurvival.stats.GearBonusReader].
 *
 * Every mutating call rebuilds the lore and the glint override so the visible
 * item always matches its stored enchants.
 */
object EnchantData {

    @JvmField
    val ENCHANTS_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:enchantments")!!

    private val gson = Gson()
    private val mapType = object : TypeToken<Map<String, Int>>() {}.type

    fun read(item: ItemStack): Map<CustomEnchant, Int> {
        val meta = item.itemMeta ?: return emptyMap()
        val json = meta.persistentDataContainer.get(ENCHANTS_KEY, PersistentDataType.STRING)
            ?: return emptyMap()
        return try {
            val raw: Map<String, Int> = gson.fromJson(json, mapType)
            raw.mapNotNull { (id, level) -> EnchantRegistry.get(id)?.let { it to level } }.toMap()
        } catch (_: Exception) {
            emptyMap()
        }
    }

    fun levelOf(item: ItemStack, enchant: CustomEnchant): Int = read(item)[enchant] ?: 0

    fun hasUltimate(item: ItemStack): Boolean = read(item).keys.any { it.ultimate }

    /** Adds or overwrites [enchant] at [level]. Pass a non-positive level to remove it. */
    fun set(item: ItemStack, enchant: CustomEnchant, level: Int) {
        val current = read(item).toMutableMap()
        if (level <= 0) current.remove(enchant) else current[enchant] = level.coerceAtMost(enchant.maxLevel)
        writeAll(item, current)
    }

    fun remove(item: ItemStack, enchant: CustomEnchant) = set(item, enchant, 0)

    private fun writeAll(item: ItemStack, enchants: Map<CustomEnchant, Int>) {
        val meta = item.itemMeta ?: return
        if (enchants.isEmpty()) {
            meta.persistentDataContainer.remove(ENCHANTS_KEY)
        } else {
            val raw = enchants.entries.associate { (e, l) -> e.id to l }
            meta.persistentDataContainer.set(ENCHANTS_KEY, PersistentDataType.STRING, gson.toJson(raw))
        }
        meta.setEnchantmentGlintOverride(enchants.isNotEmpty())
        item.itemMeta = meta
        ItemLoreGenerator.refreshLore(item)
    }
}
