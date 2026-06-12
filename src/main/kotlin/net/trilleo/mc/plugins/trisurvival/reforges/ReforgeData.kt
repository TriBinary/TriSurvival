package net.trilleo.mc.plugins.trisurvival.reforges

import net.trilleo.mc.plugins.trisurvival.items.ItemLoreGenerator
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Reads and writes the single reforge stored on an item. The reforge id lives in
 * the item PDC under [REFORGE_KEY]; only the id is persisted — the scaled stats are
 * derived from the reforge plus the item's current rarity at read time, so a rarity
 * change (e.g. via the Recombobulator) rescales them automatically. Mirrors
 * [net.trilleo.mc.plugins.trisurvival.enchants.EnchantData].
 *
 * Every mutating call rebuilds the lore (and name prefix) so the visible item always
 * matches its stored reforge.
 */
object ReforgeData {

    @JvmField
    val REFORGE_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:reforge")!!

    fun read(item: ItemStack): Reforge? {
        val id = item.itemMeta?.persistentDataContainer
            ?.get(REFORGE_KEY, PersistentDataType.STRING) ?: return null
        return ReforgeRegistry.get(id)
    }

    /** Sets the reforge, replacing any previous one. */
    fun set(item: ItemStack, reforge: Reforge) {
        val meta = item.itemMeta ?: return
        meta.persistentDataContainer.set(REFORGE_KEY, PersistentDataType.STRING, reforge.id)
        item.itemMeta = meta
        ItemLoreGenerator.refreshLore(item)
    }

    fun remove(item: ItemStack) {
        val meta = item.itemMeta ?: return
        meta.persistentDataContainer.remove(REFORGE_KEY)
        item.itemMeta = meta
        ItemLoreGenerator.refreshLore(item)
    }
}
