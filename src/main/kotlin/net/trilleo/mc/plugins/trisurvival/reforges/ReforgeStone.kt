package net.trilleo.mc.plugins.trisurvival.reforges

import net.trilleo.mc.plugins.trisurvival.utils.PDCUtil
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Reader for reforge stones. A stone carries the id of the [Reforge] it applies,
 * stored in PDC under [STONE_KEY]. The concrete stone items live in the `items`
 * package as [net.trilleo.mc.plugins.trisurvival.registration.PluginItem]s and stamp
 * this key in their `customize` block.
 */
object ReforgeStone {

    @JvmField
    val STONE_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:reforge_stone")!!

    fun read(item: ItemStack): Reforge? {
        val id = PDCUtil.get(item, STONE_KEY, PersistentDataType.STRING) ?: return null
        return ReforgeRegistry.get(id)
    }

    fun isStone(item: ItemStack): Boolean = read(item) != null
}
