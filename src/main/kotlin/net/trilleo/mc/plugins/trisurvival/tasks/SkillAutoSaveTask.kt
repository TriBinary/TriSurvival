package net.trilleo.mc.plugins.trisurvival.tasks

import net.trilleo.mc.plugins.trisurvival.registration.PluginTask
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager

class SkillAutoSaveTask : PluginTask(delay = 1200L, period = 1200L, async = true) {

    override fun run() {
        SkillManager.saveDirty()
    }
}
