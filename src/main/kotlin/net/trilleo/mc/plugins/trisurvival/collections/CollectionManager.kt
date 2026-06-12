package net.trilleo.mc.plugins.trisurvival.collections

import com.google.gson.JsonObject
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.data.PlayerData
import net.trilleo.mc.plugins.trisurvival.data.PlayerDataManager
import net.trilleo.mc.plugins.trisurvival.events.CollectionIncrementEvent
import net.trilleo.mc.plugins.trisurvival.events.CollectionTierUpEvent
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * Owns per-player collection progress. Amounts are persisted inside each player's [PlayerData] JSON
 * under the `"collections"` object (`collectionId -> Long`), so saving/loading is handled by
 * [PlayerDataManager] — no separate store or lifecycle is needed.
 *
 * [increment] runs on the main thread, fires [CollectionIncrementEvent] (cancellable), then fires
 * [CollectionTierUpEvent] and applies any tier rewards for each tier crossed.
 */
object CollectionManager {

    private const val DATA_KEY = "collections"
    private val mm = MiniMessage.miniMessage()
    private lateinit var plugin: JavaPlugin

    fun init(plugin: JavaPlugin) {
        this.plugin = plugin
        plugin.logger.info("CollectionManager initialised")
    }

    /** Returns the player's running total for [collectionId]. */
    fun getAmount(player: Player, collectionId: String): Long {
        val store = readStore(PlayerDataManager.get(player)) ?: return 0L
        return store.get(collectionId)?.let { runCatching { it.asLong }.getOrDefault(0L) } ?: 0L
    }

    /** Returns the player's current tier for [collectionId] (0 if the collection is unknown). */
    fun getTier(player: Player, collectionId: String): Int {
        val collection = CollectionRegistry.get(collectionId) ?: return 0
        return collection.tierFor(getAmount(player, collectionId))
    }

    /** Progress (0.0–1.0) toward the next tier; 1.0 when the max tier is reached. */
    fun getProgress(player: Player, collection: Collection): Double {
        val amount = getAmount(player, collection.id)
        val tier = collection.tierFor(amount)
        if (tier >= collection.maxTier) return 1.0
        val previous = if (tier == 0) 0L else collection.tiers[tier - 1]
        val next = collection.tiers[tier]
        val range = next - previous
        if (range <= 0) return 1.0
        return ((amount - previous).toDouble() / range).coerceIn(0.0, 1.0)
    }

    /** Adds [amount] to the player's [collection] total, firing events and applying tier rewards. */
    fun increment(player: Player, collection: Collection, amount: Long) {
        if (amount <= 0) return

        val event = CollectionIncrementEvent(player, collection, amount)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled || event.amount <= 0) return

        val data = PlayerDataManager.get(player)
        val store = writableStore(data)
        val old = store.get(collection.id)?.let { runCatching { it.asLong }.getOrDefault(0L) } ?: 0L
        val new = old + event.amount
        store.addProperty(collection.id, new)

        val oldTier = collection.tierFor(old)
        val newTier = collection.tierFor(new)
        if (newTier > oldTier) {
            Bukkit.getPluginManager().callEvent(CollectionTierUpEvent(player, collection, oldTier, newTier))
            for (tier in (oldTier + 1)..newTier) {
                collection.tierRewards[tier]?.apply?.invoke(player)
            }
            sendTierUpMessage(player, collection, newTier)
        }
    }

    private fun sendTierUpMessage(player: Player, collection: Collection, newTier: Int) {
        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
        player.sendMessage(Component.empty())
        player.sendMessage(
            mm.deserialize(
                "  <gold><bold>COLLECTION TIER UP <yellow>${collection.displayName} " +
                        "<gold>${CollectionFormat.roman(newTier)}"
            )
        )
        collection.tierRewards[newTier]?.description?.let { description ->
            player.sendMessage(mm.deserialize("    <gray>$description"))
        }
        player.sendMessage(Component.empty())
    }

    /** The collections object as stored, or `null` when the player has no data yet (read-only path). */
    private fun readStore(data: PlayerData): JsonObject? {
        val element = data.json.get(DATA_KEY) ?: return null
        return if (element.isJsonObject) element.asJsonObject else null
    }

    /** The collections object, creating and attaching it to the player's JSON if absent. */
    private fun writableStore(data: PlayerData): JsonObject {
        readStore(data)?.let { return it }
        val created = JsonObject()
        data.set(DATA_KEY, created)
        return created
    }
}
