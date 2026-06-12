package net.trilleo.mc.plugins.trisurvival.collections

/**
 * Helpers that generate the cumulative tier thresholds for a [Collection]. The returned list's size
 * is the collection's level cap; entry *i* is the total amount required to reach tier *i + 1*.
 *
 * Developers can pass an explicit `listOf(...)` instead for full hand-tuned control.
 */
object CollectionTiers {

    /**
     * Geometric scaling: the first tier needs [base], and every subsequent tier multiplies the
     * previous threshold by [growth]. Produces [cap] thresholds.
     *
     * `geometric(50, 2.0, 5)` -> `[50, 100, 200, 400, 800]`.
     */
    fun geometric(base: Long, growth: Double, cap: Int): List<Long> {
        require(cap >= 1) { "cap must be >= 1" }
        require(base >= 1) { "base must be >= 1" }
        val tiers = ArrayList<Long>(cap)
        var current = base.toDouble()
        repeat(cap) {
            tiers.add(current.toLong().coerceAtLeast(1))
            current *= growth
        }
        return tiers
    }

    /**
     * Linear scaling: thresholds are [base], `2*base`, `3*base`, … up to [cap] tiers.
     *
     * `linear(100, 5)` -> `[100, 200, 300, 400, 500]`.
     */
    fun linear(base: Long, cap: Int): List<Long> {
        require(cap >= 1) { "cap must be >= 1" }
        require(base >= 1) { "base must be >= 1" }
        return (1..cap).map { base * it }
    }
}
