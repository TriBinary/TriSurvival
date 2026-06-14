package net.trilleo.mc.plugins.trisurvival.stats

import org.bukkit.Material

/**
 * The kind of thing that contributes a stat bonus. This enum is the single expansion point for new
 * sources: add an entry here, write a matching
 * [net.trilleo.mc.plugins.trisurvival.stats.contributors.StatContributor], and register it.
 *
 * [color] mirrors the conventions already used in item lore (enchants light purple, reforges gray) so the
 * breakdown reads consistently with the rest of the UI.
 */
enum class StatSourceType(
    val displayName: String,
    val color: String,
    val icon: Material,
    val order: Int
) {
    BASE("Base", "<dark_gray>", Material.PAPER, 0),
    SKILL("Skills", "<green>", Material.EXPERIENCE_BOTTLE, 1),
    GEAR("Gear", "<aqua>", Material.DIAMOND_CHESTPLATE, 2),
    ENCHANT("Enchants", "<light_purple>", Material.ENCHANTED_BOOK, 3),
    REFORGE("Reforges", "<gray>", Material.ANVIL, 4);
}
