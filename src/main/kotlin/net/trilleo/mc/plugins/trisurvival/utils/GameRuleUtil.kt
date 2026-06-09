package net.trilleo.mc.plugins.trisurvival.utils

import org.bukkit.GameRule
import org.bukkit.World

object GameRuleUtil {

    fun <T : Any> get(world: World, rule: GameRule<T>): T? =
        world.getGameRuleValue(rule)

    fun <T : Any> set(world: World, rule: GameRule<T>, value: T): Boolean =
        world.setGameRule(rule, value)

    fun toggle(world: World, rule: GameRule<Boolean>): Boolean? {
        val newValue = !(world.getGameRuleValue(rule) ?: return null)
        world.setGameRule(rule, newValue)
        return newValue
    }
}
