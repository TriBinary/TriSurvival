package net.trilleo.mc.plugins.trisurvival.stats

enum class Stat(
    val displayName: String,
    val symbol: String,
    val color: String,
    val baseValue: Double
) {
    HEALTH("Health", "❤", "<red>", 100.0),
    DEFENSE("Defense", "🛡️", "<green>", 0.0),
    STRENGTH("Strength", "⚔", "<dark_red>", 0.0),
    CRIT_CHANCE("Crit Chance", "☠", "<blue>", 30.0),
    CRIT_DAMAGE("Crit Damage", "☠", "<blue>", 50.0),
    SPEED("Speed", "✦", "<white>", 100.0),
    INTELLIGENCE("Intelligence", "✨", "<aqua>", 100.0);
}
