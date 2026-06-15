package net.trilleo.mc.plugins.trisurvival.mobs.zombies

import net.trilleo.mc.plugins.trisurvival.mobs.ChanceDrop
import net.trilleo.mc.plugins.trisurvival.mobs.CustomMob
import net.trilleo.mc.plugins.trisurvival.mobs.MobRarity
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack

/** Hostile demo mob — replaces a fraction of natural overworld zombies (see MobSpawnRegistry). */
object CryptGhoul : CustomMob("crypt_ghoul") {
    override val displayName = "Crypt Ghoul"
    override val entityType = EntityType.ZOMBIE
    override val rarity = MobRarity.UNCOMMON
    override val baseXp = 15.0
    override val xpDrop = 8

    override val stats = mapOf(
        Stat.HEALTH to 300.0,
        Stat.DAMAGE to 40.0,
        Stat.DEFENSE to 20.0
    )

    override val drops = listOf(
        ChanceDrop(chance = 0.75, min = 1, max = 2) { ItemStack(Material.ROTTEN_FLESH) },
        ChanceDrop(chance = 0.05) { ItemStack(Material.IRON_INGOT) }
    )
}
