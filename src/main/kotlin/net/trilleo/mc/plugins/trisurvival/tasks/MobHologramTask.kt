package net.trilleo.mc.plugins.trisurvival.tasks

import net.trilleo.mc.plugins.trisurvival.mobs.runtime.MobManager
import net.trilleo.mc.plugins.trisurvival.registration.PluginTask

/** Refreshes custom-mob health holograms and runs mob ability ticks; prunes despawned instances. */
class MobHologramTask : PluginTask(delay = 0L, period = 4L) {
    override fun run() {
        MobManager.tick()
    }
}
