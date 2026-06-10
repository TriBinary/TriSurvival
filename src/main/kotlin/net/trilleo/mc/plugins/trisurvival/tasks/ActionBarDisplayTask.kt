package net.trilleo.mc.plugins.trisurvival.tasks

import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.registration.PluginTask
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.Bukkit

class ActionBarDisplayTask : PluginTask(delay = 0L, period = 10L) {

    private val mm = MiniMessage.miniMessage()

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            val profile = StatManager.getProfile(player)
            val hp = formatInt(player.health * 5)
            val maxHp = formatInt(profile.health)
            val def = formatInt(profile.defense)
            val mana = formatInt(profile.currentMana)
            val maxMana = formatInt(profile.maxMana)

            player.sendActionBar(
                mm.deserialize(
                    "<red>❤ ${hp}/${maxHp}     <green>❈ ${def}     <aqua>✎ ${mana}/${maxMana}"
                )
            )
        }
    }

    private fun formatInt(value: Double): String = value.toInt().toString()
}
