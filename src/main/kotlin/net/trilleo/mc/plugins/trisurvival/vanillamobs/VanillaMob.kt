package net.trilleo.mc.plugins.trisurvival.vanillamobs

import net.trilleo.mc.plugins.trisurvival.mobs.CustomMob
import net.trilleo.mc.plugins.trisurvival.mobs.MobRarity
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import org.bukkit.entity.EntityType

/**
 * A data-driven custom-mob port of a vanilla entity, registered in bulk by [VanillaMobs]. Every port
 * is [MobRarity.COMMON] and carries the vanilla entity's health (and melee damage, when it has any).
 *
 * This class lives **outside** the auto-scanned `mobs` package on purpose: it has a parameterised
 * constructor, so it must not be picked up by `MobRegistrar`'s package scan — it is registered
 * programmatically instead.
 */
class VanillaMob(
    id: String,
    override val displayName: String,
    override val entityType: EntityType,
    private val health: Double,
    private val attackDamage: Double
) : CustomMob(id) {

    override val rarity: MobRarity = MobRarity.COMMON

    override val baseXp: Double = (health * 0.5 + attackDamage).coerceAtLeast(2.0)

    override val stats: Map<Stat, Double> = buildMap {
        put(Stat.HEALTH, health)
        if (attackDamage > 0.0) put(Stat.DAMAGE, attackDamage)
    }
}
