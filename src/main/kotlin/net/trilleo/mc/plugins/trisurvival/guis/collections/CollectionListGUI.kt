package net.trilleo.mc.plugins.trisurvival.guis.collections

import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.collections.Collection
import net.trilleo.mc.plugins.trisurvival.collections.CollectionFormat
import net.trilleo.mc.plugins.trisurvival.collections.CollectionManager
import net.trilleo.mc.plugins.trisurvival.collections.CollectionRegistry
import net.trilleo.mc.plugins.trisurvival.enums.FillMode
import net.trilleo.mc.plugins.trisurvival.registration.PagedPluginGUI
import net.trilleo.mc.plugins.trisurvival.utils.itemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/** Paged list of the collections in the selected category, each showing progress and current tier. */
class CollectionListGUI : PagedPluginGUI(
    id = "collection_list",
    title = MiniMessage.miniMessage().deserialize("<dark_gray>Collections"),
    rows = 6,
    fillMode = FillMode.DARK
) {

    override fun backTarget(player: Player): String? = "collections"

    override fun getItems(player: Player): List<ItemStack> {
        val category = CollectionSession.getCategory(player.uniqueId) ?: return emptyList()
        return CollectionRegistry.byCategory(category).map { collection -> render(player, collection) }
    }

    private fun render(player: Player, collection: Collection): ItemStack {
        val amount = CollectionManager.getAmount(player, collection.id)
        val tier = collection.tierFor(amount)
        val next = collection.nextThreshold(amount)
        val tierLabel = if (tier > 0) " <gray>${CollectionFormat.roman(tier)}" else ""

        val lore = mutableListOf(
            "<gray>Collected: <white>${format(amount)}",
            "<gray>Tier: <white>$tier<gray>/${collection.maxTier}"
        )
        if (next != null) {
            val progress = CollectionManager.getProgress(player, collection)
            lore.add("<gray>Next tier: <white>${format(next)}")
            lore.add("  ${progressBar(progress)} <gray>${String.format("%.1f", progress * 100)}%")
        } else {
            lore.add("<green>Maxed out!")
        }

        return itemStack(collection.icon) {
            name("<yellow><bold>${collection.displayName}$tierLabel")
            lore(*lore.toTypedArray())
        }
    }

    private fun progressBar(progress: Double): String {
        val total = 20
        val filled = (progress * total).toInt().coerceIn(0, total)
        return "<green>${"■".repeat(filled)}<dark_gray>${"■".repeat(total - filled)}"
    }

    private fun format(value: Long): String = "%,d".format(value)
}
