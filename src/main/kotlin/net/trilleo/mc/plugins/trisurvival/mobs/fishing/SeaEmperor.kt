package net.trilleo.mc.plugins.trisurvival.mobs.fishing

import net.trilleo.mc.plugins.trisurvival.fishing.SeaCreature
import net.trilleo.mc.plugins.trisurvival.mobs.ChanceDrop
import net.trilleo.mc.plugins.trisurvival.mobs.MobAbility
import net.trilleo.mc.plugins.trisurvival.mobs.MobRarity
import net.trilleo.mc.plugins.trisurvival.mobs.abilities.FrenzyAbility
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

/** Legendary fishing boss — high stats, a frenzy ability, and rich loot. */
object SeaEmperor : SeaCreature("sea_emperor") {
    override val displayName = "Sea Emperor"
    override val entityType = EntityType.ELDER_GUARDIAN
    override val rarity = MobRarity.LEGENDARY
    override val baseXp = 500.0

    override val stats = mapOf(
        Stat.HEALTH to 5000.0,
        Stat.DAMAGE to 200.0,
        Stat.DEFENSE to 100.0
    )

    override val abilities: List<MobAbility> = listOf(FrenzyAbility())

    override val drops = listOf(
        ChanceDrop(chance = 1.0) { ItemStack(Material.HEART_OF_THE_SEA) },
        ChanceDrop(chance = 0.5, min = 2, max = 5) { ItemStack(Material.PRISMARINE_CRYSTALS) }
    )

    override fun onFishedUp(entity: LivingEntity, player: Player) {
        (entity as? Mob)?.target = player
        entity.velocity = Vector(0.0, 0.5, 0.0)
    }
}
