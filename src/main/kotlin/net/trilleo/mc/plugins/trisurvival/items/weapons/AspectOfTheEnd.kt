package net.trilleo.mc.plugins.trisurvival.items.weapons

import net.trilleo.mc.plugins.trisurvival.items.AbilityTrigger
import net.trilleo.mc.plugins.trisurvival.items.ItemAbility
import net.trilleo.mc.plugins.trisurvival.items.ItemRarity
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.utils.ItemStackBuilder
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.Sound

object AspectOfTheEnd : PluginItem("aspect_of_the_end") {

    override val displayName = "Aspect of the End"
    override val material = Material.DIAMOND_SWORD
    override val rarity = ItemRarity.RARE
    override val type = ItemType.SWORD

    override val statBonuses = mapOf(
        Stat.DAMAGE to 100.0,
        Stat.STRENGTH to 100.0,
        Stat.CRIT_CHANCE to 10.0
    )

    override val abilities = listOf(
        ItemAbility(
            name = "Instant Transmission",
            trigger = AbilityTrigger.RIGHT_CLICK,
            description = "Teleport <green>8 blocks</green> ahead of you and gain <yellow>+50 ${Stat.SPEED.symbol} Speed</yellow> for <green>3 seconds</green>.",
            manaCost = 50,
            cooldownTicks = 20,
            action = { player ->
                val result = player.world.rayTraceBlocks(
                    player.eyeLocation,
                    player.eyeLocation.direction,
                    8.0,
                    FluidCollisionMode.NEVER,
                    true
                )
                val destination = result?.hitPosition?.toLocation(player.world)
                    ?: player.eyeLocation.add(player.eyeLocation.direction.multiply(8.0))

                destination.yaw = player.location.yaw
                destination.pitch = player.location.pitch
                player.teleport(destination)
                player.world.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
            }
        )
    )

    override fun customize(builder: ItemStackBuilder) {
        builder.glint(true)
        builder.unbreakable(true)
    }
}
