package net.trilleo.mc.plugins.trisurvival.ores.customOres.pureOres

import net.trilleo.mc.plugins.trisurvival.ores.CustomOre
import net.trilleo.mc.plugins.trisurvival.ores.CustomOres
import net.trilleo.mc.plugins.trisurvival.ores.ToolTier
import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.World
import kotlin.random.Random

object PureDiamond : CustomOre("pure_diamond") {

    override val representingBlock: Material = Material.DIAMOND_BLOCK
    override val blockStrength: Int = 400
    override val dropVanillaItem: Material = Material.DIAMOND
    override val baseDropAmount: Int = 3
    override val skillXp: Double = 25.0
    override val expOrbDrop: Int = 3
    override val requiresPickaxe: Boolean = true
    override val minToolTier: ToolTier = ToolTier.IRON

    private val replaceable = setOf(Material.STONE, Material.DEEPSLATE)

    override fun generate(chunk: Chunk, world: World, random: Random) {
        val attempts = random.nextInt(1, 3)
        repeat(attempts) {
            val baseX = random.nextInt(16)
            val baseZ = random.nextInt(16)
            val baseY = random.nextInt(-64, 0)
            val veinSize = random.nextInt(2, 4)
            repeat(veinSize) {
                val block = chunk.getBlock(
                    (baseX + random.nextInt(-1, 2)).coerceIn(0, 15),
                    (baseY + random.nextInt(-1, 2)).coerceIn(world.minHeight, world.maxHeight - 1),
                    (baseZ + random.nextInt(-1, 2)).coerceIn(0, 15)
                )
                if (block.type in replaceable) CustomOres.place(block, this)
            }
        }
    }
}