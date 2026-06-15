package net.trilleo.mc.plugins.trisurvival.mobs.spawn

/**
 * A spherical region that periodically spawns custom mobs from its [mobIds] pool, up to [cap] live
 * mobs inside the zone. Persisted in server data so zones survive restarts.
 */
data class MobZone(
    val name: String,
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val radius: Double,
    val cap: Int,
    val mobIds: List<String>
)
