package net.trilleo.mc.plugins.trisurvival.mining

import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData

/**
 * Plays the vanilla block-break particles and sound for a block. The custom mining engine breaks
 * blocks programmatically (which does not always emit the destroy effect), so this restores the
 * visual/audio feedback. [data] must be captured before the block is set to air.
 */
object BreakEffects {

    fun play(block: Block, data: BlockData) {
        val world = block.world
        val center = block.location.add(0.5, 0.5, 0.5)
        world.spawnParticle(Particle.BLOCK, center, 30, 0.3, 0.3, 0.3, 0.0, data)
        world.playSound(center, data.soundGroup.breakSound, 1.0f, 1.0f)
    }
}
