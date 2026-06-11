package net.trilleo.mc.plugins.trisurvival.guis

import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.enchants.EnchantBook
import net.trilleo.mc.plugins.trisurvival.enchants.EnchantData
import net.trilleo.mc.plugins.trisurvival.enums.FillMode
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.registration.PluginGUI
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.skills.Skill
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
import net.trilleo.mc.plugins.trisurvival.utils.itemStack
import org.bukkit.Bukkit
import org.bukkit.Material
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
 * Custom anvil for enchant books. Place gear on the left and a book on the right
 * to apply the book's enchant, or two identical books to combine them into the
 * next level. Costs vanilla experience levels. Enforces the one-Ultimate-per-item
 * rule and enchant conflicts.
 */
class EnchantAnvilGUI(private val plugin: JavaPlugin) : PluginGUI(
    id = "enchant_anvil",
    title = MiniMessage.miniMessage().deserialize("<dark_gray>Enchant Anvil"),
    rows = 3,
    fillMode = FillMode.DARK
) {

    companion object {
        const val LEFT_SLOT = 10
        const val PLUS_SLOT = 11
        const val RIGHT_SLOT = 12
        const val ARROW_SLOT = 14
        const val RESULT_SLOT = 16
        const val INFO_SLOT = 22
        val INPUT_SLOTS = intArrayOf(LEFT_SLOT, RIGHT_SLOT)
    }

    private data class AnvilResult(val result: ItemStack, val cost: Int)

    private val mm = MiniMessage.miniMessage()

    override fun setup(player: Player, inventory: Inventory) {
        inventory.setItem(LEFT_SLOT, null)
        inventory.setItem(RIGHT_SLOT, null)
        inventory.setItem(PLUS_SLOT, itemStack(Material.NETHER_STAR) {
            name("<gray>+")
            hideTooltip(true)
        })
        inventory.setItem(ARROW_SLOT, itemStack(Material.ARROW) {
            name("<gray>➜")
            hideTooltip(true)
        })
        render(player, inventory)
    }

    override fun onClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val rawSlot = event.rawSlot

        when {
            rawSlot in INPUT_SLOTS -> scheduleRender(player, event.inventory)
            rawSlot == RESULT_SLOT -> {
                event.isCancelled = true
                execute(player, event.inventory)
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

    private fun shiftIntoInput(player: Player, event: InventoryClickEvent) {
        val clicked = event.currentItem?.takeIf { !it.type.isAir } ?: return
        val inventory = event.inventory
        val preferred = if (EnchantBook.isBook(clicked)) RIGHT_SLOT else LEFT_SLOT
        val fallback = if (preferred == RIGHT_SLOT) LEFT_SLOT else RIGHT_SLOT
        val slot = when {
            inventory.getItem(preferred).isNullOrAir() -> preferred
            inventory.getItem(fallback).isNullOrAir() -> fallback
            else -> preferred
        }
        val current = inventory.getItem(slot)
        inventory.setItem(slot, clicked.clone())
        player.inventory.setItem(event.slot, current?.takeIf { !it.type.isAir })
        scheduleRender(player, inventory)
    }

    private fun ItemStack?.isNullOrAir(): Boolean = this == null || this.type.isAir

    override fun onDrag(event: InventoryDragEvent) {
        val topSize = event.inventory.size
        if (event.rawSlots.any { it < topSize && it !in INPUT_SLOTS }) {
            event.isCancelled = true
            return
        }
        if (event.rawSlots.any { it in INPUT_SLOTS }) {
            val player = event.whoClicked as? Player ?: return
            scheduleRender(player, event.inventory)
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        for (slot in INPUT_SLOTS) {
            val item = event.inventory.getItem(slot) ?: continue
            event.inventory.setItem(slot, null)
            val leftover = player.inventory.addItem(item)
            for (drop in leftover.values) player.world.dropItemNaturally(player.location, drop)
        }
    }

    private fun scheduleRender(player: Player, inventory: Inventory) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (player.isOnline) render(player, inventory)
        })
    }

    private fun render(player: Player, inventory: Inventory) {
        val match = compute(inventory)
        if (match == null) {
            inventory.setItem(RESULT_SLOT, placeholderPane())
            inventory.setItem(INFO_SLOT, null)
            return
        }
        inventory.setItem(RESULT_SLOT, match.result)
        val affordable = player.level >= match.cost
        val color = if (affordable) "<green>" else "<red>"
        inventory.setItem(INFO_SLOT, itemStack(Material.EXPERIENCE_BOTTLE) {
            name("${color}Cost: ${match.cost} levels")
            lore("<dark_gray>Click the result to confirm")
        })
    }

    private fun execute(player: Player, inventory: Inventory) {
        val match = compute(inventory) ?: return
        if (player.level < match.cost) {
            player.sendMessage(mm.deserialize("<red>You need ${match.cost} experience levels."))
            player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
            return
        }

        player.level -= match.cost
        consumeOne(inventory, LEFT_SLOT)
        consumeOne(inventory, RIGHT_SLOT)

        val result = match.result
        val cursor = player.itemOnCursor
        if (cursor.type.isAir) {
            player.setItemOnCursor(result)
        } else {
            val leftover = player.inventory.addItem(result)
            for (drop in leftover.values) player.world.dropItemNaturally(player.location, drop)
        }

        SkillManager.addXP(player, Skill.ENCHANTING, match.cost * 5.0)
        player.playSound(player.location, Sound.BLOCK_ANVIL_USE, 1f, 1.2f)
        render(player, inventory)
    }

    private fun compute(inventory: Inventory): AnvilResult? {
        val left = inventory.getItem(LEFT_SLOT)?.takeIf { !it.type.isAir } ?: return null
        val right = inventory.getItem(RIGHT_SLOT)?.takeIf { !it.type.isAir } ?: return null

        val leftBook = EnchantBook.read(left)
        val rightBook = EnchantBook.read(right)

        // Combine two identical books into the next level.
        if (leftBook != null && rightBook != null) {
            val (le, ll) = leftBook
            val (re, rl) = rightBook
            if (le == re && ll == rl && ll < le.maxLevel) {
                return AnvilResult(EnchantBook.create(le, ll + 1), (ll + 1) * 3)
            }
            return null
        }

        // Apply a book to gear.
        if (leftBook == null && rightBook != null) {
            val (enchant, level) = rightBook
            val type = itemType(left)
            if (!enchant.appliesTo(type)) return null

            val current = EnchantData.read(left)
            if (current.keys.any { it != enchant && it.conflictsWith(enchant) }) return null
            if (enchant.ultimate && current.keys.any { it.ultimate && it != enchant }) return null
            if (level <= (current[enchant] ?: 0)) return null

            val result = left.clone()
            result.amount = 1
            EnchantData.set(result, enchant, level)
            return AnvilResult(result, enchant.xpCost(level))
        }

        return null
    }

    private fun consumeOne(inventory: Inventory, slot: Int) {
        val item = inventory.getItem(slot) ?: return
        if (item.amount > 1) item.amount -= 1 else inventory.setItem(slot, null)
    }

    private fun itemType(item: ItemStack): ItemType {
        val name = item.itemMeta?.persistentDataContainer
            ?.get(PluginItem.ITEM_TYPE_KEY, PersistentDataType.STRING) ?: return ItemType.NONE
        return runCatching { ItemType.valueOf(name) }.getOrDefault(ItemType.NONE)
    }
}
