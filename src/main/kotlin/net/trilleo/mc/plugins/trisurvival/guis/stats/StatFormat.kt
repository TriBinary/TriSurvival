package net.trilleo.mc.plugins.trisurvival.guis.stats

import net.trilleo.mc.plugins.trisurvival.stats.Stat

/** Shared number/value formatting for the stats GUIs. */
object StatFormat {

    /** Whole numbers print without a decimal; otherwise one decimal place. */
    fun number(value: Double): String =
        if (value == value.toLong().toDouble()) value.toLong().toString()
        else String.format("%.1f", value)

    /** A stat's value with its `%` suffix when the stat is a percentage. */
    fun value(stat: Stat, value: Double): String =
        number(value) + if (stat.isPercentage) "%" else ""

    /** A signed contribution amount with the stat's `%` suffix, e.g. `+12` or `-3.5%`. */
    fun signed(stat: Stat, value: Double): String {
        val sign = if (value >= 0) "+" else ""
        return sign + value(stat, value)
    }
}
