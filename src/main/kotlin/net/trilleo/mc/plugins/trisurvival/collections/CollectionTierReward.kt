package net.trilleo.mc.plugins.trisurvival.collections

import org.bukkit.entity.Player

/**
 * Optional payload granted when a player reaches a collection tier. [description] is shown in the
 * tier-up message; [apply] runs once on the player at the moment the tier is crossed (e.g. to grant
 * a stat bonus, unlock something, or send a custom message). Both are optional.
 */
class CollectionTierReward(
    val description: String? = null,
    val apply: ((Player) -> Unit)? = null
)
