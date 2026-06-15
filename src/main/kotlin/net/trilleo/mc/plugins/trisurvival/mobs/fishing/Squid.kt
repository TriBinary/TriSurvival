package net.trilleo.mc.plugins.trisurvival.mobs.fishing

import net.trilleo.mc.plugins.trisurvival.fishing.SeaCreature
import net.trilleo.mc.plugins.trisurvival.mobs.ChanceDrop
import net.trilleo.mc.plugins.trisurvival.mobs.MobRarity
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack

/** Common sea creature reeled up while fishing. */
object Squid : SeaCreature("sea_squid") {
    override val displayName = "Squid"
    override val entityType = EntityType.SQUID
    override val rarity = MobRarity.COMMON
    override val baseXp = 20.0

    override val stats = mapOf(
        Stat.HEALTH to 100.0,
        Stat.DAMAGE to 10.0
    )

    override val drops = listOf(
        ChanceDrop(chance = 1.0, min = 1, max = 2) { ItemStack(Material.INK_SAC) }
    )
}
