package net.trilleo.mc.plugins.trisurvival.guis.stats

import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.enums.FillMode
import net.trilleo.mc.plugins.trisurvival.registration.GUIManager
import net.trilleo.mc.plugins.trisurvival.registration.PluginGUI
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatContribution
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import net.trilleo.mc.plugins.trisurvival.stats.StatSourceType
import net.trilleo.mc.plugins.trisurvival.utils.itemStack
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

/** Detail view for a single stat selected via [StatSession]: total, base, and a per-source breakdown. */
class StatDetailGUI : PluginGUI(
    id = "stat_detail",
    title = MiniMessage.miniMessage().deserialize("<dark_gray>Stat Breakdown"),
    rows = 6,
    fillMode = FillMode.DARK
) {

    override fun setup(player: Player, inventory: Inventory) {
        val stat = StatSession.getStat(player.uniqueId)
        if (stat == null) {
            inventory.setItem(SUMMARY_SLOT, itemStack(Material.BARRIER) { name("<red>No stat selected") })
            inventory.setItem(BACK_SLOT, backButton())
            return
        }

        val profile = StatManager.getProfile(player)
        inventory.setItem(SUMMARY_SLOT, summary(stat, profile[stat]))

        val byType = profile.breakdown.byTypeForStat(stat)
        val centerStart = ROW_THREE_START + (ROW_SIZE - byType.size).coerceAtLeast(0) / 2
        byType.entries.forEachIndexed { i, (type, contributions) ->
            inventory.setItem(centerStart + i, sourceIcon(stat, type, contributions))
        }

        inventory.setItem(BACK_SLOT, backButton())
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        if (event.rawSlot == BACK_SLOT) {
            val player = event.whoClicked as? Player ?: return
            GUIManager.open(player, "stats")
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        (event.player as? Player)?.let { StatSession.clear(it.uniqueId) }
    }

    private fun summary(stat: Stat, total: Double): org.bukkit.inventory.ItemStack =
        itemStack(stat.category.icon) {
            name("${stat.color}${stat.symbol} <bold>${stat.displayName}")
            lore(
                "",
                "<gray>Total: <white>${StatFormat.value(stat, total)}",
                "<gray>Base: <white>${StatFormat.value(stat, stat.baseValue)}",
                "<gray>Category: <white>${stat.category.displayName}",
                ""
            )
        }

    private fun sourceIcon(
        stat: Stat,
        type: StatSourceType,
        contributions: List<StatContribution>
    ): org.bukkit.inventory.ItemStack {
        val typeTotal = contributions.sumOf { it.bonuses[stat] ?: 0.0 }
        val lore = mutableListOf("")
        for (contribution in contributions) {
            val amount = contribution.bonuses[stat] ?: 0.0
            lore.add("<gray>${contribution.label}: ${type.color}${StatFormat.signed(stat, amount)}")
        }
        return itemStack(type.icon) {
            name("${type.color}<bold>${type.displayName} <gray>(${StatFormat.signed(stat, typeTotal)})")
            lore(*lore.toTypedArray())
        }
    }

    private fun backButton(): org.bukkit.inventory.ItemStack = itemStack(Material.ARROW) {
        name("<yellow>Back to Stats")
    }

    companion object {
        private const val ROW_SIZE = 9
        private const val SUMMARY_SLOT = 4
        private const val ROW_THREE_START = 3 * ROW_SIZE
        private const val BACK_SLOT = 45
    }
}
