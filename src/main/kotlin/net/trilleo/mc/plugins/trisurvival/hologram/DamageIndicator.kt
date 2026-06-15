package net.trilleo.mc.plugins.trisurvival.hologram

import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.Main
import org.bukkit.Color
import org.bukkit.entity.Display
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.TextDisplay
import org.bukkit.plugin.java.JavaPlugin
import kotlin.random.Random

/**
 * Short-lived rising damage popup shown when a custom mob is hit, Hypixel-style. Normal hits render in
 * grey, crits in bold red with sparkles. The popup drifts upward for a few ticks, then despawns.
 */
object DamageIndicator {

    private val mm = MiniMessage.miniMessage()
    private const val LIFETIME_TICKS = 16L

    fun spawn(target: LivingEntity, amount: Double, isCrit: Boolean) {
        val plugin = JavaPlugin.getPlugin(Main::class.java)
        val world = target.world
        val loc = target.location.add(
            Random.nextDouble(-0.4, 0.4),
            target.height + 0.6 + Random.nextDouble(0.0, 0.3),
            Random.nextDouble(-0.4, 0.4)
        )
        val rounded = amount.toInt()
        val text = if (isCrit) "<bold><red>✧ $rounded ✧" else "<gray>$rounded"

        val display = world.spawn(loc, TextDisplay::class.java) { d ->
            d.text(mm.deserialize(text))
            d.billboard = Display.Billboard.CENTER
            d.isSeeThrough = true
            d.isShadowed = false
            d.backgroundColor = Color.fromARGB(0, 0, 0, 0)
            d.isPersistent = false
        }

        // Drift upward, then despawn.
        object : Runnable {
            var ticks = 0L
            override fun run() {
                if (display.isDead || ticks >= LIFETIME_TICKS) {
                    if (!display.isDead) display.remove()
                    return
                }
                ticks++
                display.teleport(display.location.add(0.0, 0.06, 0.0))
                plugin.server.scheduler.runTaskLater(plugin, this, 1L)
            }
        }.run()
    }
}
