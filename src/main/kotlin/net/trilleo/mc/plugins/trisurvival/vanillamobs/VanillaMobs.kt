package net.trilleo.mc.plugins.trisurvival.vanillamobs

import net.trilleo.mc.plugins.trisurvival.mobs.CustomMob
import net.trilleo.mc.plugins.trisurvival.registration.MobRegistrar
import org.bukkit.entity.EntityType
import org.bukkit.plugin.java.JavaPlugin

/**
 * Bulk-registers a [VanillaMob] for (almost) every living vanilla entity, as a COMMON-rarity port, and
 * exposes the [EntityType] → custom-mob lookup the spawn-replace listener uses to swap natural/spawner
 * spawns for their custom equivalents.
 *
 * Bosses are intentionally excluded (Ender Dragon, Wither, Warden, Elder Guardian).
 */
object VanillaMobs {

    private const val ID_PREFIX = "vanilla_"

    // EntityType to (vanilla health, melee attack damage). 0 damage = no/ranged attack (kept passive).
    private val specs: Map<EntityType, Pair<Double, Double>> = linkedMapOf(
        // Passive & neutral
        EntityType.ALLAY to (20.0 to 0.0),
        EntityType.ARMADILLO to (12.0 to 0.0),
        EntityType.AXOLOTL to (14.0 to 2.0),
        EntityType.BAT to (6.0 to 0.0),
        EntityType.BEE to (10.0 to 2.0),
        EntityType.CAMEL to (32.0 to 0.0),
        EntityType.CAT to (10.0 to 0.0),
        EntityType.CHICKEN to (4.0 to 0.0),
        EntityType.COD to (3.0 to 0.0),
        EntityType.COW to (10.0 to 0.0),
        EntityType.DOLPHIN to (10.0 to 3.0),
        EntityType.DONKEY to (22.0 to 0.0),
        EntityType.FOX to (10.0 to 2.0),
        EntityType.FROG to (10.0 to 0.0),
        EntityType.GLOW_SQUID to (10.0 to 0.0),
        EntityType.GOAT to (10.0 to 2.0),
        EntityType.HORSE to (22.0 to 0.0),
        EntityType.IRON_GOLEM to (100.0 to 14.0),
        EntityType.LLAMA to (22.0 to 1.0),
        EntityType.MOOSHROOM to (10.0 to 0.0),
        EntityType.MULE to (22.0 to 0.0),
        EntityType.OCELOT to (10.0 to 0.0),
        EntityType.PANDA to (20.0 to 4.0),
        EntityType.PARROT to (6.0 to 0.0),
        EntityType.PIG to (10.0 to 0.0),
        EntityType.POLAR_BEAR to (30.0 to 6.0),
        EntityType.PUFFERFISH to (3.0 to 0.0),
        EntityType.RABBIT to (3.0 to 0.0),
        EntityType.SALMON to (3.0 to 0.0),
        EntityType.SHEEP to (8.0 to 0.0),
        EntityType.SKELETON_HORSE to (15.0 to 0.0),
        EntityType.SNIFFER to (14.0 to 0.0),
        EntityType.SNOW_GOLEM to (4.0 to 0.0),
        EntityType.SQUID to (10.0 to 0.0),
        EntityType.STRIDER to (20.0 to 0.0),
        EntityType.TADPOLE to (6.0 to 0.0),
        EntityType.TRADER_LLAMA to (22.0 to 1.0),
        EntityType.TROPICAL_FISH to (3.0 to 0.0),
        EntityType.TURTLE to (30.0 to 0.0),
        EntityType.VILLAGER to (20.0 to 0.0),
        EntityType.WANDERING_TRADER to (20.0 to 0.0),
        EntityType.WOLF to (8.0 to 4.0),
        EntityType.ZOMBIE_HORSE to (15.0 to 0.0),

        // Hostile
        EntityType.BLAZE to (20.0 to 6.0),
        EntityType.BOGGED to (16.0 to 0.0),
        EntityType.BREEZE to (30.0 to 0.0),
        EntityType.CAVE_SPIDER to (12.0 to 2.0),
        EntityType.CREEPER to (20.0 to 0.0),
        EntityType.DROWNED to (20.0 to 3.0),
        EntityType.ENDERMAN to (40.0 to 7.0),
        EntityType.ENDERMITE to (8.0 to 2.0),
        EntityType.EVOKER to (24.0 to 6.0),
        EntityType.GHAST to (10.0 to 0.0),
        EntityType.GUARDIAN to (30.0 to 6.0),
        EntityType.HOGLIN to (40.0 to 8.0),
        EntityType.HUSK to (20.0 to 3.0),
        EntityType.ILLUSIONER to (32.0 to 0.0),
        EntityType.MAGMA_CUBE to (16.0 to 4.0),
        EntityType.PHANTOM to (20.0 to 2.0),
        EntityType.PIGLIN to (16.0 to 5.0),
        EntityType.PIGLIN_BRUTE to (50.0 to 13.0),
        EntityType.PILLAGER to (24.0 to 0.0),
        EntityType.RAVAGER to (100.0 to 12.0),
        EntityType.SHULKER to (30.0 to 4.0),
        EntityType.SILVERFISH to (8.0 to 1.0),
        EntityType.SKELETON to (20.0 to 0.0),
        EntityType.SLIME to (16.0 to 4.0),
        EntityType.SPIDER to (16.0 to 2.0),
        EntityType.STRAY to (20.0 to 0.0),
        EntityType.VEX to (14.0 to 9.0),
        EntityType.VINDICATOR to (24.0 to 13.0),
        EntityType.WITCH to (26.0 to 0.0),
        EntityType.WITHER_SKELETON to (20.0 to 8.0),
        EntityType.ZOGLIN to (40.0 to 6.0),
        EntityType.ZOMBIE to (20.0 to 3.0),
        EntityType.ZOMBIE_VILLAGER to (20.0 to 3.0),
        EntityType.ZOMBIFIED_PIGLIN to (20.0 to 5.0)
    )

    private val byType = HashMap<EntityType, CustomMob>()

    fun registerAll(plugin: JavaPlugin) {
        byType.clear()
        var count = 0
        for ((type, spec) in specs) {
            val id = ID_PREFIX + type.name.lowercase()
            val mob = VanillaMob(id, prettify(type), type, spec.first, spec.second)
            if (MobRegistrar.register(mob)) {
                byType[type] = mob
                count++
            }
        }
        plugin.logger.info("Registered $count vanilla mob port(s)")
    }

    /** The COMMON custom port for [type], or `null` if that type isn't ported (e.g. a boss). */
    fun get(type: EntityType): CustomMob? = byType[type]

    private fun prettify(type: EntityType): String =
        type.name.split('_').joinToString(" ") { part ->
            part.lowercase().replaceFirstChar { it.uppercase() }
        }
}
