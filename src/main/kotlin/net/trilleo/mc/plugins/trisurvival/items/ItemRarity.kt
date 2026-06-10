package net.trilleo.mc.plugins.trisurvival.items

enum class ItemRarity(
    val displayName: String,
    val color: String,
    val bold: Boolean = false
) {
    COMMON("COMMON", "<white>"),
    UNCOMMON("UNCOMMON", "<green>"),
    RARE("RARE", "<blue>"),
    EPIC("EPIC", "<dark_purple>"),
    LEGENDARY("LEGENDARY", "<gold>", bold = true),
    MYTHIC("MYTHIC", "<light_purple>", bold = true),
    DIVINE("DIVINE", "<aqua>", bold = true),
    SPECIAL("SPECIAL", "<red>", bold = true),
    VERY_SPECIAL("VERY SPECIAL", "<red>", bold = true);

    val formattedName: String
        get() = if (bold) "$color<bold>$displayName" else "$color$displayName"
}
