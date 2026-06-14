package net.trilleo.mc.plugins.trisurvival.guis.stats

import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.enums.FillMode
import net.trilleo.mc.plugins.trisurvival.enums.PagedGUIMode
import net.trilleo.mc.plugins.trisurvival.registration.GUIManager
import net.trilleo.mc.plugins.trisurvival.registration.PagedPluginGUI
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatCategory
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import net.trilleo.mc.plugins.trisurvival.utils.itemStack
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/** Overview of every stat's value, one category per page. Clicking a stat opens its breakdown. */
class StatsOverviewGUI : PagedPluginGUI(
    id = "stats",
    title = MiniMessage.miniMessage().deserialize("<dark_gray>Stats"),
    rows = 6,
    fillMode = FillMode.DARK,
    mode = PagedGUIMode.SET
) {

    private val categories = StatCategory.entries.sortedBy { it.order }

    override fun getSetItems(player: Player): Map<Int, Map<Int, ItemStack>> {
        val profile = StatManager.getProfile(player)
        return categories.withIndex().associate { (page, category) ->
            val slots = mutableMapOf<Int, ItemStack>()
            slots[HEADER_SLOT] = header(category)
            statsOf(category).forEachIndexed { i, stat ->
                if (i < CONTENT_SLOTS.size) slots[CONTENT_SLOTS[i]] = statIcon(profile[stat], stat)
            }
            page to slots
        }
    }

    override fun onContentClick(event: InventoryClickEvent, page: Int) {
        val player = event.whoClicked as? Player ?: return
        val category = categories.getOrNull(page) ?: return
        val index = CONTENT_SLOTS.indexOf(event.rawSlot)
        if (index < 0) return
        val stat = statsOf(category).getOrNull(index) ?: return
        StatSession.setStat(player.uniqueId, stat)
        GUIManager.open(player, "stat_detail")
    }

    private fun statsOf(category: StatCategory): List<Stat> =
        Stat.entries.filter { it.category == category }

    private fun header(category: StatCategory): ItemStack = itemStack(category.icon) {
        name("<gold><bold>${category.displayName}")
        lore("<gray>Stats in this category", "<dark_gray>Click a stat to view its breakdown")
    }

    private fun statIcon(value: Double, stat: Stat): ItemStack = itemStack(stat.category.icon) {
        name("${stat.color}${stat.symbol} <bold>${stat.displayName}")
        lore(
            "",
            "${stat.color}${stat.symbol} ${stat.displayName}: <white>${StatFormat.value(stat, value)}",
            "",
            "<yellow>Click to view breakdown"
        )
    }

    companion object {
        private const val HEADER_SLOT = 4

        // Inner grid: rows 1–3, columns 1–7 of the 6-row menu — leaves a dark border and the nav row free.
        private val CONTENT_SLOTS = buildList {
            for (row in 1..3) for (col in 1..7) add(row * 9 + col)
        }
    }
}
