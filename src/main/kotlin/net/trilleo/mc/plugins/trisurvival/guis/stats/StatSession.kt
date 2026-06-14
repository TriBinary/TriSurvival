package net.trilleo.mc.plugins.trisurvival.guis.stats

import net.trilleo.mc.plugins.trisurvival.stats.Stat
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Carries the selected [Stat] from the overview GUI to the detail GUI, since
 * [net.trilleo.mc.plugins.trisurvival.registration.GUIManager.open] only takes a GUI id.
 */
object StatSession {

    private val selected = ConcurrentHashMap<UUID, Stat>()

    fun setStat(uuid: UUID, stat: Stat) {
        selected[uuid] = stat
    }

    fun getStat(uuid: UUID): Stat? = selected[uuid]

    fun clear(uuid: UUID) {
        selected.remove(uuid)
    }
}
