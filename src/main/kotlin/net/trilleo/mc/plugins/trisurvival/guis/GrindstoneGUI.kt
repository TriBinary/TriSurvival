package net.trilleo.mc.plugins.trisurvival.guis

import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.enchants.EnchantData
import net.trilleo.mc.plugins.trisurvival.enchants.EnchantRegistry
import net.trilleo.mc.plugins.trisurvival.enums.FillMode
import net.trilleo.mc.plugins.trisurvival.registration.PluginGUI
import net.trilleo.mc.plugins.trisurvival.utils.RomanNumeral
import net.trilleo.mc.plugins.trisurvival.utils.itemStack
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

/**
 * Custom grindstone. Place an enchanted item to list its enchants as buttons;
 * clicking a button strips that enchant from the item. Paginates when an item
 * carries more enchants than fit on one page.
 */
class GrindstoneGUI(private val plugin: JavaPlugin) : PluginGUI(
    id = "grindstone",
    title = MiniMessage.miniMessage().deserialize("<dark_gray>Remove Enchants"),
    rows = 6,
    fillMode = FillMode.DARK
) {

    companion object {
        const val INPUT_SLOT = 19

        // 3 rows × 5 columns option grid on the right of the input slot.
        val REMOVE_SLOTS = intArrayOf(
            12, 13, 14, 15, 16,
            21, 22, 23, 24, 25,
            30, 31, 32, 33, 34
        )

        const val PREV_SLOT = 40
        const val PAGE_SLOT = 41
        const val NEXT_SLOT = 42

        val REMOVE_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:enchant_remove")!!
    }

    private val pages = mutableMapOf<UUID, Int>()

    override fun setup(player: Player, inventory: Inventory) {
        inventory.setItem(INPUT_SLOT, null)
        pages[player.uniqueId] = 0
        render(player, inventory)
    }

    override fun onClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val rawSlot = event.rawSlot

        when {
            rawSlot == INPUT_SLOT -> {
                pages[player.uniqueId] = 0
                scheduleRender(player, event.inventory)
            }

            rawSlot in REMOVE_SLOTS -> {
                event.isCancelled = true
                handleRemove(player, event.inventory, event.currentItem)
            }

            rawSlot == PREV_SLOT -> {
                event.isCancelled = true
                changePage(player, event.inventory, -1)
            }

            rawSlot == NEXT_SLOT -> {
                event.isCancelled = true
                changePage(player, event.inventory, 1)
            }

            rawSlot >= event.inventory.size -> {
                if (event.isShiftClick) {
                    event.isCancelled = true
                    shiftIntoInput(player, event)
                }
            }

            else -> event.isCancelled = true
        }
    }

    override fun onDrag(event: InventoryDragEvent) {
        val topSize = event.inventory.size
        if (event.rawSlots.any { it < topSize && it != INPUT_SLOT }) {
            event.isCancelled = true
            return
        }
        if (event.rawSlots.any { it == INPUT_SLOT }) {
            val player = event.whoClicked as? Player ?: return
            scheduleRender(player, event.inventory)
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        pages.remove(player.uniqueId)
        val item = event.inventory.getItem(INPUT_SLOT) ?: return
        event.inventory.setItem(INPUT_SLOT, null)
        val leftover = player.inventory.addItem(item)
        for (drop in leftover.values) player.world.dropItemNaturally(player.location, drop)
    }

    private fun shiftIntoInput(player: Player, event: InventoryClickEvent) {
        val clicked = event.currentItem?.takeIf { !it.type.isAir } ?: return
        val inventory = event.inventory
        val current = inventory.getItem(INPUT_SLOT)
        inventory.setItem(INPUT_SLOT, clicked.clone())
        player.inventory.setItem(event.slot, current?.takeIf { !it.type.isAir })
        pages[player.uniqueId] = 0
        scheduleRender(player, inventory)
    }

    private fun scheduleRender(player: Player, inventory: Inventory) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (player.isOnline) render(player, inventory)
        })
    }

    private fun changePage(player: Player, inventory: Inventory, delta: Int) {
        pages[player.uniqueId] = (pages[player.uniqueId] ?: 0) + delta
        render(player, inventory)
        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f)
    }

    private fun render(player: Player, inventory: Inventory) {
        for (slot in REMOVE_SLOTS) inventory.setItem(slot, placeholderPane())
        inventory.setItem(PREV_SLOT, placeholderPane())
        inventory.setItem(PAGE_SLOT, placeholderPane())
        inventory.setItem(NEXT_SLOT, placeholderPane())

        val input = inventory.getItem(INPUT_SLOT)?.takeIf { !it.type.isAir } ?: return
        val enchants = EnchantData.read(input).entries.toList()

        val pageSize = REMOVE_SLOTS.size
        val totalPages = maxOf(1, (enchants.size + pageSize - 1) / pageSize)
        val page = (pages[player.uniqueId] ?: 0).coerceIn(0, totalPages - 1)
        pages[player.uniqueId] = page

        val start = page * pageSize
        val end = minOf(start + pageSize, enchants.size)
        for (i in start until end) {
            val (enchant, level) = enchants[i]
            val color = if (enchant.ultimate) "<light_purple><bold>" else "<blue>"
            inventory.setItem(REMOVE_SLOTS[i - start], itemStack(Material.GRINDSTONE) {
                name("<red>Remove: $color${enchant.displayName} ${RomanNumeral.toRoman(level)}")
                lore("<dark_gray>Click to strip this enchant")
                pdc(REMOVE_KEY, PersistentDataType.STRING, enchant.id)
            })
        }

        if (totalPages > 1) {
            if (page > 0) inventory.setItem(PREV_SLOT, navItem("<yellow>Previous Page"))
            inventory.setItem(PAGE_SLOT, itemStack(Material.PAPER) { name("<white>Page ${page + 1}/$totalPages") })
            if (page < totalPages - 1) inventory.setItem(NEXT_SLOT, navItem("<yellow>Next Page"))
        }
    }

    private fun handleRemove(player: Player, inventory: Inventory, clicked: ItemStack?) {
        val id = clicked?.itemMeta?.persistentDataContainer
            ?.get(REMOVE_KEY, PersistentDataType.STRING) ?: return
        val enchant = EnchantRegistry.get(id) ?: return
        val input = inventory.getItem(INPUT_SLOT)?.takeIf { !it.type.isAir } ?: return

        EnchantData.remove(input, enchant)
        inventory.setItem(INPUT_SLOT, input)
        player.playSound(player.location, Sound.BLOCK_GRINDSTONE_USE, 1f, 1f)
        render(player, inventory)
    }

    private fun navItem(name: String): ItemStack = itemStack(Material.ARROW) { name(name) }
}
