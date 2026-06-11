package net.trilleo.mc.plugins.trisurvival.items

import org.bukkit.entity.Player

data class ItemAbility(
    val name: String,
    val trigger: AbilityTrigger,
    val description: String,
    val manaCost: Int = 0,
    val cooldownTicks: Int = 0,
    val action: (Player) -> Unit = {}
)

enum class AbilityTrigger(val displayName: String) {
    RIGHT_CLICK("RIGHT CLICK"),
    LEFT_CLICK("LEFT CLICK"),
    SNEAK("SNEAK"),
    PASSIVE("PASSIVE");
}
