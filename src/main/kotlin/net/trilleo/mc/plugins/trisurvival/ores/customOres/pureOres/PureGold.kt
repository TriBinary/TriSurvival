package net.trilleo.mc.plugins.trisurvival.ores.customOres.pureOres

import net.trilleo.mc.plugins.trisurvival.ores.CustomOre
import net.trilleo.mc.plugins.trisurvival.ores.CustomOres
import net.trilleo.mc.plugins.trisurvival.ores.ToolTier
import org.bukkit.Chunk
import org.bukkit.World
import kotlin.random.Random

object PureGold : CustomOre("pure_gold") {

    override val representingBlock = org.bukkit.Material.GOLD_BLOCK
    override val blockStrength = 300
    override val dropVanillaItem = org.bukkit.Material.GOLD_INGOT
    override val baseDropAmount = 3
    override val skillXp = 20.0
    override val expOrbDrop = 3
    override val requiresPickaxe = true
    override val minToolTier = ToolTier.IRON

    private val replaceable = setOf(org.bukkit.Material.STONE, org.bukkit.Material.DEEPSLATE)

    override fun generate(chunk: Chunk, world: World, random: Random) {
        val attempts = random.nextInt(2, 5)
        repeat(attempts) {
            val baseX = random.nextInt(16)
            val baseZ = random.nextInt(16)
            val baseY = random.nextInt(-64, 32)
            val veinSize = random.nextInt(3, 5)
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