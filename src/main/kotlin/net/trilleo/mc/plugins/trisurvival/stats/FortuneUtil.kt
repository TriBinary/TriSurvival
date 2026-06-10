package net.trilleo.mc.plugins.trisurvival.stats

import kotlin.random.Random

object FortuneUtil {

    fun rollFortune(fortuneValue: Double): Int {
        if (fortuneValue <= 0) return 0
        val guaranteed = (fortuneValue / 100.0).toInt()
        val remainder = (fortuneValue % 100.0) / 100.0
        val bonus = if (Random.nextDouble() < remainder) 1 else 0
        return guaranteed + bonus
    }
}
