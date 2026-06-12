package net.trilleo.mc.plugins.trisurvival.collections

/**
 * Supplies [Collection] definitions. Subclass this and place it anywhere inside the
 * `net.trilleo.mc.plugins.trisurvival.collections` package (or a subpackage) to have its collections
 * auto-registered at startup by [CollectionRegistry].
 *
 * The subclass must be a Kotlin `object`, or declare a no-arg / `JavaPlugin` constructor.
 *
 * ```kotlin
 * object MyCollections : CollectionProvider() {
 *     override fun collections() = listOf(
 *         Collection(
 *             id = "ruby",
 *             displayName = "Ruby",
 *             icon = Material.RED_DYE,
 *             category = CollectionCategory.MINING,
 *             sources = listOf(CollectionSource.Custom("ruby")),
 *             tiers = CollectionTiers.geometric(base = 25, growth = 2.0, cap = 9)
 *         )
 *     )
 * }
 * ```
 */
abstract class CollectionProvider {
    abstract fun collections(): List<Collection>
}
