package net.trilleo.mc.plugins.trisurvival.stats

import net.trilleo.mc.plugins.trisurvival.ores.CustomOres
import org.bukkit.Material
import org.bukkit.block.Block
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Per-block "Block Strength" — the hardness value that drives the custom mining-time formula.
 *
 * `MiningTimeTicks = floor(BlockStrength * 30 / MiningSpeed)`, never below [SOFT_CAP_TICKS] unless the
 * Mining Speed is high enough to instant-mine (>= 30x strength for blocks, 60x for ores).
 *
 * Most vanilla blocks map cleanly through the hardness-derived fallback (`hardness * 10`: stone 1.5 ->
 * 15, obsidian 50 -> 500, ores 3 -> 30), so [strengths] only lists the few that diverge from the wiki.
 */
object BlockStrength {

    const val SOFT_CAP_TICKS = 4

    /**
     * Floor applied to a player's Mining Speed so a value of 0 never yields an infinite break time.
     * Tuned so bare-hand mining is vanilla-slow (stone strength 15 -> ~150 ticks); tools and stats
     * add speed on top via [ToolSpeed] and the MINING_SPEED stat.
     */
    const val BASE_MINING_SPEED = 3.0

    /** Returned by [strengthOf] for bedrock and other blocks vanilla treats as unbreakable. */
    const val UNBREAKABLE = Int.MAX_VALUE

    private val strengths: Map<Material, Int> = mapOf(
        Material.NETHERRACK to 8,
        Material.END_STONE to 30,
        Material.OBSIDIAN to 500,
        Material.ANCIENT_DEBRIS to 1500
    )

    fun strengthOf(block: Block): Int {
        CustomOres.oreAt(block)?.let { return it.blockStrength }
        strengths[block.type]?.let { return it }

        val hardness = block.type.hardness
        if (hardness < 0) return UNBREAKABLE
        return max(1, (hardness * 10).roundToInt())
    }

    fun isOre(block: Block): Boolean =
        block.type in OreTypes.all || CustomOres.oreAt(block) != null

    /**
     * Mining time in ticks for the given [strength] and player [miningSpeed]. Returns `0` when the
     * block should break instantly. [isOre] raises the instant-mine threshold from 30x to 60x.
     */
    fun miningTimeTicks(strength: Int, miningSpeed: Double, isOre: Boolean): Int {
        if (strength == UNBREAKABLE) return Int.MAX_VALUE

        val speed = max(miningSpeed, BASE_MINING_SPEED)
        val instantThreshold = strength.toDouble() * if (isOre) 60 else 30
        if (speed >= instantThreshold) return 0

        val ticks = (strength * 30.0 / speed).toInt()
        return max(SOFT_CAP_TICKS, ticks)
    }
}
