package net.trilleo.mc.plugins.trisurvival.ores.customOres

import net.trilleo.mc.plugins.trisurvival.ores.CustomOre
import net.trilleo.mc.plugins.trisurvival.ores.CustomOres
import net.trilleo.mc.plugins.trisurvival.ores.ToolTier
import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.World
import kotlin.random.Random

object Mithril : CustomOre("mithril") {

    override val representingBlock = Material.CYAN_WOOL
    override val blockStrength = 500
    override val dropItemId = "mithril_ore"
    override val baseDropAmount = 1
    override val skillXp = 30.0
    override val expOrbDrop = 3
    override val requiresPickaxe = true
    override val minToolTier = ToolTier.IRON

    private val replaceable = setOf(Material.STONE, Material.DEEPSLATE)

    override fun generate(chunk: Chunk, world: World, random: Random) {
        val attempts = random.nextInt(0, 3)
        repeat(attempts) {
            val baseX = random.nextInt(16)
            val baseZ = random.nextInt(16)
            val baseY = random.nextInt(-32, 32)
            val veinSize = random.nextInt(6, 10)
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
