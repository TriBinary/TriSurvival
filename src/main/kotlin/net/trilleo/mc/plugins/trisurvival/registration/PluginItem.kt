package net.trilleo.mc.plugins.trisurvival.registration

import net.trilleo.mc.plugins.trisurvival.items.ItemAbility
import net.trilleo.mc.plugins.trisurvival.items.ItemLoreGenerator
import net.trilleo.mc.plugins.trisurvival.items.ItemRarity
import net.trilleo.mc.plugins.trisurvival.items.ItemType
import net.trilleo.mc.plugins.trisurvival.stats.GearBonusReader
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.utils.ItemStackBuilder
import net.trilleo.mc.plugins.trisurvival.utils.itemStack
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.persistence.PersistentDataType
import java.util.*

abstract class PluginItem(val id: String) {

    abstract val displayName: String
    abstract val material: Material

    open val rarity: ItemRarity = ItemRarity.COMMON
    open val type: ItemType = ItemType.NONE
    open val statBonuses: Map<Stat, Double> = emptyMap()
    open val abilities: List<ItemAbility> = emptyList()

    /**
     * Base64-encoded skin texture for player-head items. When set (and [material] is
     * [Material.PLAYER_HEAD]) the head renders this texture — the easy path for Hypixel-style
     * custom icons. Grab the base64 string from a site like minecraft-heads.com.
     */
    open val texture: String? = null

    /** When `true`, each created item gets a random UUID tag so otherwise-stackable items never stack. */
    open val unique: Boolean = false

    companion object {
        @JvmField
        val ITEM_ID_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:custom_item_id")!!

        @JvmField
        val ITEM_RARITY_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:item_rarity")!!

        @JvmField
        val ITEM_TYPE_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:item_type")!!

        @JvmField
        val UNIQUE_ID_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:unique_id")!!

        /** The item's un-prefixed display name, used to rebuild the name when a reforge prefix changes. */
        @JvmField
        val BASE_NAME_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:base_name")!!

        /** Marks an item as already upgraded by a Recombobulator (one upgrade per item). */
        @JvmField
        val RECOMBOBULATED_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:recombobulated")!!

        /** `true` when [stack] is a TriSurvival custom item (carries [ITEM_ID_KEY]). */
        fun isCustom(stack: ItemStack?): Boolean {
            val meta = stack?.itemMeta ?: return false
            return meta.persistentDataContainer.has(ITEM_ID_KEY, PersistentDataType.STRING)
        }
    }

    fun create(amount: Int = 1): ItemStack {
        val namePrefix = if (rarity.bold) "${rarity.color}<bold>" else rarity.color
        // Resolve the name outside the builder lambda: ItemStackBuilder also has a `displayName`
        // property, which would otherwise shadow this item's `displayName` and yield "null".
        val itemName = "$namePrefix$displayName"
        val stack = itemStack(material) {
            amount(amount)
            name(itemName)
            loreComponents(ItemLoreGenerator.generate(this@PluginItem))
            texture?.let { skullTexture(it) }
            customize(this)
        }

        val meta = stack.itemMeta ?: return stack

        meta.persistentDataContainer.set(ITEM_ID_KEY, PersistentDataType.STRING, id)
        meta.persistentDataContainer.set(ITEM_RARITY_KEY, PersistentDataType.STRING, rarity.name)
        meta.persistentDataContainer.set(ITEM_TYPE_KEY, PersistentDataType.STRING, type.name)
        meta.persistentDataContainer.set(BASE_NAME_KEY, PersistentDataType.STRING, displayName)

        if (unique) {
            meta.persistentDataContainer.set(
                UNIQUE_ID_KEY, PersistentDataType.STRING, UUID.randomUUID().toString()
            )
        }

        if (statBonuses.isNotEmpty()) {
            meta.persistentDataContainer.set(
                GearBonusReader.STAT_BONUSES_KEY,
                PersistentDataType.STRING,
                GearBonusReader.encodeBonuses(statBonuses)
            )
        }

        stack.itemMeta = meta
        return stack
    }

    open fun customize(builder: ItemStackBuilder) {}

    fun matches(stack: ItemStack): Boolean {
        val meta = stack.itemMeta ?: return false
        return meta.persistentDataContainer.get(ITEM_ID_KEY, PersistentDataType.STRING) == id
    }

    fun asChoice(): RecipeChoice.ExactChoice = RecipeChoice.ExactChoice(create(1))
}
