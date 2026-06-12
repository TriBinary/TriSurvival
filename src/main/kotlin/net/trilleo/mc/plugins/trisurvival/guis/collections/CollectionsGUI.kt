package net.trilleo.mc.plugins.trisurvival.guis.collections

import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.collections.CollectionCategory
import net.trilleo.mc.plugins.trisurvival.collections.CollectionRegistry
import net.trilleo.mc.plugins.trisurvival.enums.FillMode
import net.trilleo.mc.plugins.trisurvival.registration.GUIManager
import net.trilleo.mc.plugins.trisurvival.registration.PluginGUI
import net.trilleo.mc.plugins.trisurvival.utils.itemStack
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.persistence.PersistentDataType

/** Top-level collections menu: one icon per category, opening that category's list. */
class CollectionsGUI : PluginGUI(
    id = "collections",
    title = MiniMessage.miniMessage().deserialize("<dark_gray>Collections"),
    rows = 3,
    fillMode = FillMode.DARK
) {

    private val categorySlots = mapOf(
        CollectionCategory.COMBAT to 10,
        CollectionCategory.MINING to 11,
        CollectionCategory.FARMING to 12,
        CollectionCategory.FORAGING to 13,
        CollectionCategory.FISHING to 14
    )

    override fun setup(player: Player, inventory: Inventory) {
        for ((category, slot) in categorySlots) {
            val count = CollectionRegistry.byCategory(category).size
            inventory.setItem(slot, itemStack(category.icon) {
                name("<yellow><bold>${category.displayName}")
                lore("<gray>$count collection${if (count == 1) "" else "s"}", "", "<yellow>Click to browse")
                pdc(CollectionSession.CATEGORY_KEY, PersistentDataType.STRING, category.name)
            })
        }
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        val player = event.whoClicked as? Player ?: return
        val name = event.currentItem?.itemMeta
            ?.persistentDataContainer?.get(CollectionSession.CATEGORY_KEY, PersistentDataType.STRING)
            ?: return
        val category = runCatching { CollectionCategory.valueOf(name) }.getOrNull() ?: return
        CollectionSession.setCategory(player.uniqueId, category)
        GUIManager.open(player, "collection_list")
    }
}
