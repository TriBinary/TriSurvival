package net.trilleo.mc.plugins.trisurvival.fishing

import net.trilleo.mc.plugins.trisurvival.registration.MobRegistrar

/**
 * Thin fishing-facing view over [MobRegistrar]: exposes only the registered [SeaCreature]s and a
 * rarity-weighted roll used when a player's Sea Creature Chance triggers. Sea creatures auto-register
 * like any other custom mob, so there is nothing to register here manually.
 */
object SeaCreatureRegistry {

    fun get(id: String): SeaCreature? = MobRegistrar.get(id) as? SeaCreature

    fun getAll(): List<SeaCreature> = MobRegistrar.getAll().filterIsInstance<SeaCreature>()

    /** Weighted random sea creature by rarity, or `null` when none are registered. */
    fun rollCreature(): SeaCreature? = MobRegistrar.roll(getAll()) as? SeaCreature
}
