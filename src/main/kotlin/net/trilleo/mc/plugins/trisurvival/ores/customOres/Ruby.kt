package net.trilleo.mc.plugins.trisurvival.ores.customOres

import net.trilleo.mc.plugins.trisurvival.ores.CustomOre
import net.trilleo.mc.plugins.trisurvival.ores.CustomOres
import net.trilleo.mc.plugins.trisurvival.ores.ToolTier
import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.World
import kotlin.random.Random

object Ruby : CustomOre("ruby") {

    override val representingBlock = Material.RED_STAINED_GLASS
    override val blockStrength = 1000
    override val dropItemId = "ruby"
    override val baseDropAmount = 1
    override val skillXp = 50.0
    override val expOrbDrop = 6
    override val requiresPickaxe = true
    override val minToolTier = ToolTier.NETHERITE

    private val replaceable = setOf(Material.STONE, Material.DEEPSLATE)

    override fun generate(chunk: Chunk, world: World, random: Random) {
        val attempts = random.nextInt(0, 3)
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