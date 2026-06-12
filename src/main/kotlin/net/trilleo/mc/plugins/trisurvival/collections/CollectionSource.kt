package net.trilleo.mc.plugins.trisurvival.collections

import org.bukkit.Material

/**
 * Something whose collection counts toward a [Collection]. A collection may declare several sources
 * (e.g. an ore that drops both a vanilla and a custom item).
 */
sealed interface CollectionSource {

    /** A vanilla item [material] — matched against the materials a block/mob/catch yields. */
    data class Vanilla(val material: Material) : CollectionSource

    /** A custom plugin item, matched by its [itemId] (see `ItemRegistrar`). */
    data class Custom(val itemId: String) : CollectionSource
}
