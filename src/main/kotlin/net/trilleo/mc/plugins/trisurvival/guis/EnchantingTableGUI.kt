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

/**
 * Custom enchanting table. Placing an item lists every enchant applicable to its
 * type that the player's Enchanting level allows; choosing one applies the next
 * level (up to the enchant's table cap) for a vanilla-XP-level cost. Ultimate
 * enchants are never offered here — they are book-only.
 */
class EnchantingTableGUI(private val plugin: JavaPlugin) : PluginGUI(
    id = "enchanting_table",
    title = MiniMessage.miniMessage().deserialize("<dark_gray>Enchant Item"),
    rows = 6,
    fillMode = FillMode.DARK
) {

    companion object {
        const val INPUT_SLOT = 11
        val OPTION_SLOTS = intArrayOf(
            13, 14, 15, 16,
            22, 23, 24, 25,
            31, 32, 33, 34,
            40, 41, 42, 43
        )
        val OPTION_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:enchant_option")!!
    }

    override fun setup(player: Player, inventory: Inventory) {
        inventory.setItem(INPUT_SLOT, null)
        render(player, inventory)
    }

    override fun onClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val rawSlot = event.rawSlot

        when {
            rawSlot == INPUT_SLOT -> scheduleRender(player, event.inventory)
            rawSlot in OPTION_SLOTS -> {
                event.isCancelled = true
                handleOption(player, event.inventory, event.currentItem)
            }
            rawSlot >= event.inventory.size -> {
                // Clicks in the player inventory are left untouched.
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
            if (player.isOnline) render(player, inventory)
        })
    }

    private fun render(player: Player, inventory: Inventory) {
        for (slot in OPTION_SLOTS) inventory.setItem(slot, null)

        val input = inventory.getItem(INPUT_SLOT)?.takeIf { !it.type.isAir } ?: return

        VanillaItemConverter.convert(input)
        inventory.setItem(INPUT_SLOT, input)

        val type = itemType(input)
        if (type == ItemType.NONE) return

        val enchantingLevel = SkillManager.getLevel(player.uniqueId, Skill.ENCHANTING)
        val current = EnchantData.read(input)

        val options = EnchantRegistry.applicableTo(type)
            .filter { !it.ultimate }
            .filter { it.maxTableLevel > 0 }
            .filter { enchantingLevel >= it.skillRequirement }
            .filterNot { e -> current.keys.any { it != e && it.conflictsWith(e) } }

        var idx = 0
        for (enchant in options) {
            if (idx >= OPTION_SLOTS.size) break
            val currentLevel = current[enchant] ?: 0
            if (currentLevel >= enchant.maxTableLevel) continue
            val nextLevel = currentLevel + 1
            inventory.setItem(OPTION_SLOTS[idx], optionItem(player, enchant, nextLevel))
            idx++
        }
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
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You need $cost experience levels."))
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

    private fun itemType(item: ItemStack): ItemType {
        val name = item.itemMeta?.persistentDataContainer
            ?.get(PluginItem.ITEM_TYPE_KEY, PersistentDataType.STRING) ?: return ItemType.NONE
        return runCatching { ItemType.valueOf(name) }.getOrDefault(ItemType.NONE)
    }
}
