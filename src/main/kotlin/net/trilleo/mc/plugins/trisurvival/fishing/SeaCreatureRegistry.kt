package net.trilleo.mc.plugins.trisurvival.fishing

import kotlin.random.Random

object SeaCreatureRegistry {

    private val creatures = mutableListOf<SeaCreature>()

    fun register(creature: SeaCreature) {
        creatures.add(creature)
    }

    fun unregister(id: String) {
        creatures.removeAll { it.id == id }
    }

    fun get(id: String): SeaCreature? = creatures.find { it.id == id }

    fun getAll(): List<SeaCreature> = creatures.toList()

    fun rollCreature(): SeaCreature? {
        if (creatures.isEmpty()) return null
        val totalWeight = creatures.sumOf { it.rarity.weight }
        var roll = Random.nextInt(totalWeight)
        for (creature in creatures) {
            roll -= creature.rarity.weight
            if (roll < 0) return creature
        }
        return creatures.last()
    }

    fun clear() {
        creatures.clear()
    }
}
