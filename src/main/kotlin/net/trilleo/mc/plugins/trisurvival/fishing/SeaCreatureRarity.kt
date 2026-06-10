package net.trilleo.mc.plugins.trisurvival.fishing

enum class SeaCreatureRarity(val weight: Int, val color: String) {
    COMMON(100, "<white>"),
    UNCOMMON(50, "<green>"),
    RARE(20, "<blue>"),
    EPIC(5, "<dark_purple>"),
    LEGENDARY(1, "<gold>");
}
