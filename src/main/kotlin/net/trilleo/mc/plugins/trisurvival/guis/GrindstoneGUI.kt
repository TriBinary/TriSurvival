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

/**
 * Custom grindstone. Place an enchanted item to list its enchants as buttons;
 * clicking a button strips that enchant from the item.
 */
class GrindstoneGUI(private val plugin: JavaPlugin) : PluginGUI(
    id = "grindstone",
    title = MiniMessage.miniMessage().deserialize("<dark_gray>Remove Enchants"),
    rows = 3,
    fillMode = FillMode.DARK
) {

    companion object {
        const val INPUT_SLOT = 10
        val REMOVE_SLOTS = intArrayOf(12, 13, 14, 15, 16, 21, 22, 23, 24, 25)
        val REMOVE_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:enchant_remove")!!
    }

    override fun setup(player: Player, inventory: Inventory) {
        inventory.setItem(INPUT_SLOT, null)
        render(inventory)
    }

    override fun onClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val rawSlot = event.rawSlot

        when {
            rawSlot == INPUT_SLOT -> scheduleRender(player, event.inventory)
            rawSlot in REMOVE_SLOTS -> {
                event.isCancelled = true
                handleRemove(player, event.inventory, event.currentItem)
            }
            rawSlot >= event.inventory.size -> {
                // Player inventory untouched.
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
        val item = event.inventory.getItem(INPUT_SLOT) ?: return
        event.inventory.setItem(INPUT_SLOT, null)
        val leftover = player.inventory.addItem(item)
        for (drop in leftover.values) player.world.dropItemNaturally(player.location, drop)
    }

    private fun scheduleRender(player: Player, inventory: Inventory) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (player.isOnline) render(inventory)
        })
    }

    private fun render(inventory: Inventory) {
        for (slot in REMOVE_SLOTS) inventory.setItem(slot, null)

        val input = inventory.getItem(INPUT_SLOT)?.takeIf { !it.type.isAir } ?: return
        val enchants = EnchantData.read(input)

        var idx = 0
        for ((enchant, level) in enchants) {
            if (idx >= REMOVE_SLOTS.size) break
            val color = if (enchant.ultimate) "<light_purple><bold>" else "<blue>"
            inventory.setItem(REMOVE_SLOTS[idx], itemStack(Material.GRINDSTONE) {
                name("<red>Remove: $color${enchant.displayName} ${RomanNumeral.toRoman(level)}")
                lore("<dark_gray>Click to strip this enchant")
                pdc(REMOVE_KEY, PersistentDataType.STRING, enchant.id)
            })
            idx++
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
        render(inventory)
    }
}
