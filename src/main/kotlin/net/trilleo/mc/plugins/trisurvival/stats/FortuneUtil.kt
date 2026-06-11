package net.trilleo.mc.plugins.trisurvival.stats

import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

object FortuneUtil {

    fun rollFortune(fortuneValue: Double): Int {
        if (fortuneValue <= 0) return 0
        val guaranteed = (fortuneValue / 100.0).toInt()
        val remainder = (fortuneValue % 100.0) / 100.0
        val bonus = if (Random.nextDouble() < remainder) 1 else 0
        return guaranteed + bonus
    }

    fun dropExtra(block: Block, drops: Collection<ItemStack>, fortuneValue: Double) {
        val extra = rollFortune(fortuneValue)
        if (extra <= 0) return
        val world = block.world
        val location = block.location
        for (drop in drops) {
            repeat(extra) {
                world.dropItemNaturally(location, drop.clone())
            }
        }
    }
}
