package net.trilleo.mc.plugins.trisurvival.mobs.spawn

import org.bukkit.entity.EntityType

/**
 * A rule for converting naturally-spawning vanilla mobs into custom ones.
 *
 * @param worlds   world names this rule applies to; empty matches any world.
 * @param replaces vanilla entity types this rule may replace; empty matches any type.
 * @param mobIds   the custom-mob ID pool to roll from (weighted by rarity).
 * @param chance   probability (0.0–1.0) that a matching natural spawn is converted.
 */
class MobSpawnRule(
    val worlds: Set<String> = emptySet(),
    val replaces: Set<EntityType> = emptySet(),
    val mobIds: List<String>,
    val chance: Double
)
