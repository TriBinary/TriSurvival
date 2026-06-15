package net.trilleo.mc.plugins.trisurvival.mobs.fishing

import net.trilleo.mc.plugins.trisurvival.fishing.SeaCreature
import net.trilleo.mc.plugins.trisurvival.mobs.ChanceDrop
import net.trilleo.mc.plugins.trisurvival.mobs.MobRarity
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

/** Rare, aggressive sea creature that lunges at the angler when reeled up. */
object Shark : SeaCreature("sea_shark") {
    override val displayName = "Shark"
    override val entityType = EntityType.DROWNED
    override val rarity = MobRarity.RARE
    override val baseXp = 80.0
    override val xpDrop = 25

    override val stats = mapOf(
        Stat.HEALTH to 800.0,
        Stat.DAMAGE to 80.0,
        Stat.DEFENSE to 30.0
    )

    override val drops = listOf(
        ChanceDrop(chance = 0.4, min = 1, max = 2) { ItemStack(Material.PRISMARINE_SHARD) },
        ChanceDrop(chance = 0.05) { ItemStack(Material.NAUTILUS_SHELL) }
    )

    override fun onFishedUp(entity: LivingEntity, player: Player) {
        (entity as? Mob)?.target = player
        entity.velocity = Vector(0.0, 0.45, 0.0)
    }
}
