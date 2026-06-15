package net.trilleo.mc.plugins.trisurvival.hologram

import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.entity.Display
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.TextDisplay
import org.bukkit.util.Transformation
import org.joml.Quaternionf
import org.joml.Vector3f

/**
 * A floating text label rendered with a native [TextDisplay] (Paper 1.21). The display is mounted as a
 * passenger of its host entity so it follows movement for free; a transform translation lifts it above
 * the mob's head. Call [update] to change the text and [remove] to despawn it.
 */
class Hologram private constructor(val display: TextDisplay) {

    fun update(text: Component) {
        if (!display.isDead) display.text(text)
    }

    fun remove() {
        if (!display.isDead) display.remove()
    }

    companion object {

        /** Spawns a hologram a little above [mount]'s head. */
        fun spawnAbove(mount: LivingEntity, initial: Component): Hologram {
            // The display rides as a passenger (already seated near the mob's top), so this is only the
            // small extra lift above the head — not the full mob height.
            val yOffset = 0.3f
            val display = mount.world.spawn(mount.location, TextDisplay::class.java) { d ->
                d.text(initial)
                d.billboard = Display.Billboard.CENTER
                d.isSeeThrough = false
                d.isShadowed = false
                d.alignment = TextDisplay.TextAlignment.CENTER
                d.backgroundColor = Color.fromARGB(0, 0, 0, 0)
                d.isPersistent = false
                d.transformation = Transformation(
                    Vector3f(0f, yOffset, 0f),
                    Quaternionf(),
                    Vector3f(1f, 1f, 1f),
                    Quaternionf()
                )
            }
            mount.addPassenger(display)
            return Hologram(display)
        }
    }
}
