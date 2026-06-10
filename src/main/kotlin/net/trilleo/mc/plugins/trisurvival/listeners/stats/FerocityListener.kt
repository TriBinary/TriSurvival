package net.trilleo.mc.plugins.trisurvival.listeners.stats

import net.trilleo.mc.plugins.trisurvival.stats.FortuneUtil
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class FerocityListener(private val plugin: JavaPlugin) : Listener {

    private val FEROCITY_HIT_KEY = NamespacedKey.fromString("trisurvival:ferocity_hit")!!

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onDamage(event: EntityDamageByEntityEvent) {
        val attacker = event.damager as? Player ?: return
        val target = event.entity as? LivingEntity ?: return

        if (attacker.persistentDataContainer.has(FEROCITY_HIT_KEY)) return

        val ferocity = StatManager.getStat(attacker, Stat.FEROCITY)
        val extraHits = FortuneUtil.rollFortune(ferocity)
        if (extraHits <= 0) return

        val damagePerHit = event.finalDamage

        attacker.persistentDataContainer.set(FEROCITY_HIT_KEY, PersistentDataType.BYTE, 1)
        Bukkit.getScheduler().runTask(plugin, Runnable {
            repeat(extraHits) {
                if (!target.isDead && target.isValid) {
                    target.damage(damagePerHit, attacker)
                }
            }
            attacker.persistentDataContainer.remove(FEROCITY_HIT_KEY)
        })
    }
}
