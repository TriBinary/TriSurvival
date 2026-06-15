package net.trilleo.mc.plugins.trisurvival.mobs

/**
 * Shared rarity tier for every custom mob (land, passive, or sea creature).
 *
 * [weight] drives weighted spawn rolls — higher means more common. [color] is a MiniMessage tag
 * used for the mob's health hologram name and any chat announcements.
 */
enum class MobRarity(val weight: Int, val color: String) {
    COMMON(100, "<white>"),
    UNCOMMON(50, "<green>"),
    RARE(20, "<blue>"),
    EPIC(5, "<dark_purple>"),
    LEGENDARY(1, "<gold>");
}
