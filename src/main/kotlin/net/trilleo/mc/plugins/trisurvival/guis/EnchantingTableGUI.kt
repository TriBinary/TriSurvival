package net.trilleo.mc.plugins.trisurvival.guis

import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.enchants.CustomEnchant
import net.trilleo.mc.plugins.trisurvival.enchants.EnchantData
import net.trilleo.mc.plugins.trisurvival.enchants.EnchantRegistry
import net.trilleo.mc.plugins.trisurvival.enums.FillMode
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.items.VanillaItemConverter
import net.trilleo.mc.plugins.trisurvival.registration.PluginGUI
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.skills.Skill
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
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
 * Custom enchanting table. Placing an item lists every enchant applicable to its
 * type that the player's Enchanting level allows; choosing one applies the next
 * level (up to the enchant's table cap) for a vanilla-XP-level cost. Ultimate
 * enchants are never offered here — they are book-only. Options paginate when
 * they exceed one page.
 */
class EnchantingTableGUI(private val plugin: JavaPlugin) : PluginGUI(
    id = "enchanting_table",
    title = MiniMessage.miniMessage().deserialize("<dark_gray>Enchant Item"),
    rows = 6,
    fillMode = FillMode.DARK
) {

    companion object {
        const val INPUT_SLOT = 19

        // 3 rows × 5 columns option grid on the right of the input slot.
        val OPTION_SLOTS = intArrayOf(
            12, 13, 14, 15, 16,
            21, 22, 23, 24, 25,
            30, 31, 32, 33, 34
        )

        const val PREV_SLOT = 48
        const val PAGE_SLOT = 49
        const val NEXT_SLOT = 50

        val OPTION_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:enchant_option")!!
    }

    private val mm = MiniMessage.miniMessage()
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
            rawSlot in OPTION_SLOTS -> {
                event.isCancelled = true
                handleOption(player, event.inventory, event.currentItem)
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

    private fun shiftIntoInput(player: Player, event: InventoryClickEvent) {
        val clicked = event.currentItem?.takeIf { !it.type.isAir } ?: return
        val inventory = event.inventory
        val current = inventory.getItem(INPUT_SLOT)
        inventory.setItem(INPUT_SLOT, clicked.clone())
        player.inventory.setItem(event.slot, current?.takeIf { !it.type.isAir })
        pages[player.uniqueId] = 0
        scheduleRender(player, inventory)
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
        for (slot in OPTION_SLOTS) inventory.setItem(slot, placeholderPane())
        inventory.setItem(PREV_SLOT, placeholderPane())
        inventory.setItem(PAGE_SLOT, placeholderPane())
        inventory.setItem(NEXT_SLOT, placeholderPane())

        val input = inventory.getItem(INPUT_SLOT)?.takeIf { !it.type.isAir } ?: return

        VanillaItemConverter.convert(input)
        inventory.setItem(INPUT_SLOT, input)

        val type = itemType(input)
        if (type == ItemType.NONE) return

        val options = availableOptions(player, input, type)

        val pageSize = OPTION_SLOTS.size
        val totalPages = maxOf(1, (options.size + pageSize - 1) / pageSize)
        val page = (pages[player.uniqueId] ?: 0).coerceIn(0, totalPages - 1)
        pages[player.uniqueId] = page

        val start = page * pageSize
        val end = minOf(start + pageSize, options.size)
        for (i in start until end) {
            inventory.setItem(OPTION_SLOTS[i - start], options[i])
        }

        if (totalPages > 1) {
            if (page > 0) inventory.setItem(PREV_SLOT, navItem(Material.ARROW, "<yellow>Previous Page"))
            inventory.setItem(PAGE_SLOT, navItem(Material.PAPER, "<white>Page ${page + 1}/$totalPages"))
            if (page < totalPages - 1) inventory.setItem(NEXT_SLOT, navItem(Material.ARROW, "<yellow>Next Page"))
        }
    }

    /** Applicable, skill-met, non-ultimate, non-conflicting, not-yet-table-maxed enchants. */
    private fun availableOptions(player: Player, input: ItemStack, type: ItemType): List<ItemStack> {
        val enchantingLevel = SkillManager.getLevel(player.uniqueId, Skill.ENCHANTING)
        val current = EnchantData.read(input)

        return EnchantRegistry.applicableTo(type)
            .asSequence()
            .filter { !it.ultimate }
            .filter { it.maxTableLevel > 0 }
            .filter { enchantingLevel >= it.skillRequirement }
            .filterNot { e -> current.keys.any { it != e && it.conflictsWith(e) } }
            .mapNotNull { enchant ->
                val currentLevel = current[enchant] ?: 0
                if (currentLevel >= enchant.maxTableLevel) null
                else optionItem(player, enchant, currentLevel + 1)
            }
            .toList()
    }

    private fun handleOption(player: Player, inventory: Inventory, clicked: ItemStack?) {
        val raw = clicked?.let {
            it.itemMeta?.persistentDataContainer?.get(OPTION_KEY, PersistentDataType.STRING)
        } ?: return
        val sep = raw.lastIndexOf(':')
        val enchant = EnchantRegistry.get(raw.substring(0, sep)) ?: return
        val level = raw.substring(sep + 1).toIntOrNull() ?: return

        val input = inventory.getItem(INPUT_SLOT)?.takeIf { !it.type.isAir } ?: return
        val cost = enchant.xpCost(level)
        if (player.level < cost) {
            player.sendMessage(mm.deserialize("<red>You need $cost experience levels."))
            player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
            return
        }

        player.level -= cost
        EnchantData.set(input, enchant, level)
        inventory.setItem(INPUT_SLOT, input)
        SkillManager.addXP(player, Skill.ENCHANTING, cost * 5.0)
        player.playSound(player.location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f)
        render(player, inventory)
    }

    private fun optionItem(player: Player, enchant: CustomEnchant, level: Int): ItemStack {
        val cost = enchant.xpCost(level)
        val affordable = player.level >= cost
        val costColor = if (affordable) "<green>" else "<red>"
        return itemStack(Material.ENCHANTED_BOOK) {
            name("<blue>${enchant.displayName} ${RomanNumeral.toRoman(level)}")
            lore(
                "<gray>${enchant.description(level)}",
                "",
                "${costColor}Cost: $cost levels",
                "<dark_gray>Click to enchant"
            )
            pdc(OPTION_KEY, PersistentDataType.STRING, "${enchant.id}:$level")
        }
    }

    private fun navItem(material: Material, name: String): ItemStack = itemStack(material) {
        name(name)
        hideTooltip(false)
    }

    private fun itemType(item: ItemStack): ItemType {
        val name = item.itemMeta?.persistentDataContainer
            ?.get(PluginItem.ITEM_TYPE_KEY, PersistentDataType.STRING) ?: return ItemType.NONE
        return runCatching { ItemType.valueOf(name) }.getOrDefault(ItemType.NONE)
    }
}
