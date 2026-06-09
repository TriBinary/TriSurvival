package net.trilleo.mc.plugins.trisurvival.utils

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType

object PDCUtil {

    fun <P : Any, C : Any> set(
        holder: PersistentDataHolder,
        key: NamespacedKey,
        type: PersistentDataType<P, C>,
        value: C
    ) {
        holder.persistentDataContainer.set(key, type, value)
    }

    fun <P : Any, C : Any> get(holder: PersistentDataHolder, key: NamespacedKey, type: PersistentDataType<P, C>): C? =
        holder.persistentDataContainer.get(key, type)

    fun has(holder: PersistentDataHolder, key: NamespacedKey): Boolean =
        holder.persistentDataContainer.has(key)

    fun remove(holder: PersistentDataHolder, key: NamespacedKey) {
        holder.persistentDataContainer.remove(key)
    }

    fun keys(holder: PersistentDataHolder): Set<NamespacedKey> =
        holder.persistentDataContainer.keys.toSet()

    fun <P : Any, C : Any> set(item: ItemStack, key: NamespacedKey, type: PersistentDataType<P, C>, value: C) {
        val meta = item.itemMeta ?: return
        meta.persistentDataContainer.set(key, type, value)
        item.itemMeta = meta
    }

    fun <P : Any, C : Any> get(item: ItemStack, key: NamespacedKey, type: PersistentDataType<P, C>): C? =
        item.itemMeta?.persistentDataContainer?.get(key, type)

    fun has(item: ItemStack, key: NamespacedKey): Boolean =
        item.itemMeta?.persistentDataContainer?.has(key) ?: false

    fun remove(item: ItemStack, key: NamespacedKey) {
        val meta = item.itemMeta ?: return
        meta.persistentDataContainer.remove(key)
        item.itemMeta = meta
    }

    fun keys(item: ItemStack): Set<NamespacedKey> =
        item.itemMeta?.persistentDataContainer?.keys?.toSet() ?: emptySet()
}
