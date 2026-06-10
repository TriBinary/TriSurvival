package net.trilleo.mc.plugins.trisurvival.fishing

import org.bukkit.Location
import org.bukkit.entity.Player

abstract class SeaCreature(
    val id: String,
    val displayName: String,
    val rarity: SeaCreatureRarity
) {
    abstract fun spawn(location: Location, player: Player)
}
