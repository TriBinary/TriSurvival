package net.trilleo.mc.plugins.trisurvival.stats.contributors

/**
 * Holds the ordered set of [StatContributor]s consulted on every stat recalculation. Populated once from
 * `Main.onEnable`; iterating it is the single place all stat sources are summed.
 */
object StatContributorRegistry {

    private val contributors = mutableListOf<StatContributor>()

    val all: List<StatContributor>
        get() = contributors

    fun register(contributor: StatContributor) {
        contributors.add(contributor)
        contributors.sortBy { it.order }
    }
}
