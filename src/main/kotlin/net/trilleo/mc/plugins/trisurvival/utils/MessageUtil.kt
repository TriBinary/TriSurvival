package net.trilleo.mc.plugins.trisurvival.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player

object MessageUtil {

    private val mm = MiniMessage.miniMessage()
    private var prefixComponent: Component = Component.empty()

    fun init(prefixString: String) {
        prefixComponent = mm.deserialize(prefixString)
    }

    fun sendPrefixed(player: Player, message: String) {
        player.sendMessage(build(mm.deserialize(message)))
    }

    fun sendPrefixed(player: Player, message: Component) {
        player.sendMessage(build(message))
    }

    private fun build(message: Component): Component =
        Component.text()
            .append(prefixComponent)
            .append(Component.space())
            .append(message)
            .build()
}

fun Player.sendPrefixed(message: String) = MessageUtil.sendPrefixed(this, message)

fun Player.sendPrefixed(message: Component) = MessageUtil.sendPrefixed(this, message)
