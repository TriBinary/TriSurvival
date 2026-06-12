package net.trilleo.mc.plugins.trisurvival.guis

import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.crafting.CraftingRecipeRegistry
import net.trilleo.mc.plugins.trisurvival.enums.FillMode
import net.trilleo.mc.plugins.trisurvival.items.VanillaItemConverter
import net.trilleo.mc.plugins.trisurvival.registration.GUIManager
import net.trilleo.mc.plugins.trisurvival.registration.PluginGUI
import net.trilleo.mc.plugins.trisurvival.utils.itemStack
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class CraftingTableGUI(private val plugin: JavaPlugin) : PluginGUI(
    id = "crafting_table",
    title = MiniMessage.miniMessage().deserialize("<dark_gray>Crafting Table"),
    rows = 6,
    fillMode = FillMode.DARK
) {

    companion object {
        val INPUT_SLOTS = intArrayOf(10, 11, 12, 19, 20, 21, 28, 29, 30)
        const val ARROW_SLOT = 23
        const val RESULT_SLOT = 25
        const val BOOK_SLOT = 32
    }

    override fun setup(player: Player, inventory: Inventory) {
        for (slot in INPUT_SLOTS) {
            inventory.setItem(slot, null)
        }

        inventory.setItem(ARROW_SLOT, itemStack(Material.ARROW) {
            name("<dark_gray>➜")
            hideTooltip(true)
        })

        inventory.setItem(RESULT_SLOT, null)

        inventory.setItem(BOOK_SLOT, itemStack(Material.KNOWLEDGE_BOOK) {
            name("<yellow><bold>Recipe Book")
            lore("<gray>Browse all custom recipes")
        })
    }

    override fun onClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val rawSlot = event.rawSlot
        val topInventorySize = event.inventory.size

        if (rawSlot in INPUT_SLOTS) {
            // Allow interaction with input slots, then update preview
            schedulePreviewUpdate(player, event.inventory)
            return
        }

        if (rawSlot == RESULT_SLOT) {
            event.isCancelled = true
            executeCraft(player, event.inventory)
            return
        }

        if (rawSlot == BOOK_SLOT) {
            event.isCancelled = true
            GUIManager.open(player, "recipe_book")
            return
        }

        // Player inventory slot (below the GUI)
        if (rawSlot >= topInventorySize) {
            when (event.click) {
                ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT -> {
                    event.isCancelled = true
                    smartPlace(player, event.inventory, event.currentItem ?: return, rawSlot - topInventorySize)
                    schedulePreviewUpdate(player, event.inventory)
                }
                // Prevent double-click from collecting items out of the result slot
                ClickType.DOUBLE_CLICK -> {
                    event.isCancelled = true
                }

                else -> {}
            }
            return
        }

        // All other GUI slots (filler) - cancel
        event.isCancelled = true
    }

    override fun onDrag(event: InventoryDragEvent) {
        val topSize = event.inventory.size
        val hasNonInputGUISlot = event.rawSlots.any { it < topSize && it !in INPUT_SLOTS }
        if (hasNonInputGUISlot) {
            event.isCancelled = true
            return
        }
        // If drag only touches input slots and/or player inventory, allow it
        if (event.rawSlots.any { it in INPUT_SLOTS }) {
            val player = event.whoClicked as? Player ?: return
            schedulePreviewUpdate(player, event.inventory)
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        for (slot in INPUT_SLOTS) {
            val item = event.inventory.getItem(slot) ?: continue
            event.inventory.setItem(slot, null)
            val leftover = player.inventory.addItem(item)
            for (drop in leftover.values) {
                player.world.dropItemNaturally(player.location, drop)
            }
        }
    }

    private fun readGrid(inventory: Inventory): Array<ItemStack?> {
        return Array(9) { i ->
            val item = inventory.getItem(INPUT_SLOTS[i])
            if (item == null || item.type.isAir) null else item
        }
    }

    private fun updatePreview(inventory: Inventory) {
        val grid = readGrid(inventory)
        val match = CraftingRecipeRegistry.findMatch(grid)
        val result = match?.result?.also { VanillaItemConverter.convert(it) }
        val placeholder = itemStack(Material.BARRIER) {
            hideTooltip(true)
        }
        inventory.setItem(RESULT_SLOT, result ?: if (grid.all { it == null }) null else placeholder)

    }

    private fun schedulePreviewUpdate(player: Player, inventory: Inventory) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (!player.isOnline) return@Runnable
            updatePreview(inventory)
        })
    }

    private fun executeCraft(player: Player, inventory: Inventory) {
        val grid = readGrid(inventory)
        val match = CraftingRecipeRegistry.findMatch(grid) ?: return

        for (i in 0 until 9) {
            val consume = match.consumeAmounts[i]
            if (consume <= 0) continue
            val slot = INPUT_SLOTS[i]
            val item = inventory.getItem(slot) ?: continue
            if (item.amount > consume) {
                item.amount -= consume
            } else {
                inventory.setItem(slot, null)
            }
        }

        val result = match.result.also { VanillaItemConverter.convert(it) }
        val cursor = player.itemOnCursor
        if (cursor.type.isAir) {
            player.setItemOnCursor(result)
        } else if (cursor.isSimilar(result) && cursor.amount + result.amount <= cursor.maxStackSize) {
            cursor.amount += result.amount
        } else {
            val leftover = player.inventory.addItem(result)
            for (drop in leftover.values) {
                player.world.dropItemNaturally(player.location, drop)
            }
        }

        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f)
        updatePreview(inventory)
    }

    private fun smartPlace(player: Player, guiInventory: Inventory, item: ItemStack, playerSlotIndex: Int) {
        val playerInv = player.inventory
        val actualPlayerSlot = if (playerSlotIndex < 27) playerSlotIndex + 9 else playerSlotIndex - 27

        for (guiSlot in INPUT_SLOTS) {
            val existing = guiInventory.getItem(guiSlot)
            if (existing == null || existing.type.isAir) {
                guiInventory.setItem(guiSlot, item.clone())
                playerInv.setItem(actualPlayerSlot, null)
                return
            }
            if (existing.isSimilar(item)) {
                val canFit = existing.maxStackSize - existing.amount
                if (canFit > 0) {
                    val toMove = minOf(canFit, item.amount)
                    existing.amount += toMove
                    if (item.amount > toMove) {
                        item.amount -= toMove
                        playerInv.setItem(actualPlayerSlot, item)
                    } else {
                        playerInv.setItem(actualPlayerSlot, null)
                    }
                    return
                }
            }
        }
    }
}
