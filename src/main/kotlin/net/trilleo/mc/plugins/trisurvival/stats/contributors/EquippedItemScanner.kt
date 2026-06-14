package net.trilleo.mc.plugins.trisurvival.stats.contributors

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.utils.PDCUtil
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/** An equipped (or accessory) item paired with a human-readable [label] for breakdown display. */
data class EquippedItem(val label: String, val item: ItemStack)

/**
 * Resolves the set of items whose bonuses apply to a player: the four armor slots, the held items
 * (main/off hand, filtered to weapon/tool types or unmarked items), and any accessories carried in the
 * inventory. Centralising this here removes the identical slot-iteration logic that the gear, enchant,
 * and reforge readers each used to carry.
 */
object EquippedItemScanner {

    private val HELD_TYPES = setOf(
        ItemType.SWORD, ItemType.BOW, ItemType.PICKAXE, ItemType.DRILL, ItemType.AXE,
        ItemType.HOE, ItemType.FISHING_ROD
    )

    private val plain = PlainTextComponentSerializer.plainText()

    fun scan(player: Player): List<EquippedItem> {
        val inv = player.inventory
        val result = mutableListOf<ItemStack>()

        for (armor in listOfNotNull(inv.helmet, inv.chestplate, inv.leggings, inv.boots)) {
            result.add(armor)
        }

        val mainHand = inv.itemInMainHand.takeIf { !it.type.isAir }
        val offHand = inv.itemInOffHand.takeIf { !it.type.isAir }
        for (held in listOfNotNull(mainHand, offHand)) {
            val type = itemType(held)
            if (type in HELD_TYPES || type == ItemType.NONE) result.add(held)
        }

        for (i in 0 until inv.size) {
            val item = inv.getItem(i) ?: continue
            if (item.type.isAir) continue
            if (itemType(item) == ItemType.ACCESSORY) result.add(item)
        }

        return result.map { EquippedItem(label(it), it) }
    }

    private fun itemType(item: ItemStack): ItemType {
        val name = PDCUtil.get(item, PluginItem.ITEM_TYPE_KEY, PersistentDataType.STRING)
            ?: return ItemType.NONE
        return runCatching { ItemType.valueOf(name) }.getOrDefault(ItemType.NONE)
    }

    private fun label(item: ItemStack): String {
        val name = item.itemMeta?.displayName()?.let { plain.serialize(it) }?.takeIf { it.isNotBlank() }
        return name ?: prettyMaterial(item)
    }

    private fun prettyMaterial(item: ItemStack): String =
        item.type.name.lowercase().split('_').joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercase() }
        }
}
