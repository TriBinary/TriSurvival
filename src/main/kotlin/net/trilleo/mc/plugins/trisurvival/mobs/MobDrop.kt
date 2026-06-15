package net.trilleo.mc.plugins.trisurvival.mobs

import net.trilleo.mc.plugins.trisurvival.mobs.runtime.MobInstance
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

/**
 * A loot entry rolled when a [CustomMob] dies. Implement this interface directly for weighted tables,
 * killer-conditional loot, or any custom logic; [ChanceDrop] covers the common chance + amount case.
 */
interface MobDrop {

    /** Returns the item stacks this drop contributes for a single death (may be empty). */
    fun roll(instance: MobInstance, killer: Player?): List<ItemStack>
}

/**
 * Drops [min]..[max] of an item with probability [chance] (0.0–1.0). The item is produced lazily by
 * [supplier] so custom-item drops are created fresh per kill.
 */
class ChanceDrop(
    private val chance: Double,
    private val min: Int = 1,
    private val max: Int = 1,
    private val supplier: () -> ItemStack
) : MobDrop {

    override fun roll(instance: MobInstance, killer: Player?): List<ItemStack> {
        if (Random.nextDouble() >= chance) return emptyList()
        val amount = if (max <= min) min else Random.nextInt(min, max + 1)
        if (amount <= 0) return emptyList()
        val stack = supplier()
        stack.amount = amount
        return listOf(stack)
    }
}
