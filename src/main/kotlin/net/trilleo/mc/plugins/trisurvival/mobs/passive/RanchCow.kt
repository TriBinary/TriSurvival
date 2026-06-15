package net.trilleo.mc.plugins.trisurvival.mobs.passive

import net.trilleo.mc.plugins.trisurvival.mobs.ChanceDrop
import net.trilleo.mc.plugins.trisurvival.mobs.CustomMob
import net.trilleo.mc.plugins.trisurvival.mobs.MobRarity
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack

/** Passive demo mob — custom health, rarity, hologram and drops, but no DAMAGE stat (deals no damage). */
object RanchCow : CustomMob("ranch_cow") {
    override val displayName = "Ranch Cow"
    override val entityType = EntityType.COW
    override val rarity = MobRarity.COMMON
    override val baseXp = 4.0

    override val stats = mapOf(
        Stat.HEALTH to 50.0
    )

    override val drops = listOf(
        ChanceDrop(chance = 1.0, min = 1, max = 3) { ItemStack(Material.BEEF) },
        ChanceDrop(chance = 0.5) { ItemStack(Material.LEATHER) }
    )
}
