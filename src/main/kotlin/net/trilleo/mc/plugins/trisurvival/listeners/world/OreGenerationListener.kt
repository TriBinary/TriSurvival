package net.trilleo.mc.plugins.trisurvival.listeners.world

import net.trilleo.mc.plugins.trisurvival.ores.CustomOreRegistry
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import kotlin.random.Random

/**
 * Runs each registered custom ore's world-generation hook against freshly generated chunks. The hook
 * receives a live chunk (so it can tag placed blocks via chunk PDC) and a per-chunk deterministic
 * random so regeneration is stable. Only first-generation chunks are processed.
 */
class OreGenerationListener : Listener {

    @EventHandler
    fun onChunkLoad(event: ChunkLoadEvent) {
        if (!event.isNewChunk) return

        val chunk = event.chunk
        val world = event.world
        val seed = world.seed xor (chunk.x.toLong() shl 32) xor (chunk.z.toLong() and 0xFFFFFFFFL)
        val random = Random(seed)

        for (ore in CustomOreRegistry.getAll()) {
            ore.generate(chunk, world, random)
        }
    }
}
