package net.trilleo.mc.plugins.trisurvival.registration

import com.google.common.collect.HashMultimap
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
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

abstract class PluginItem(val id: String) {

    abstract val displayName: String
    abstract val material: Material

    open val rarity: ItemRarity = ItemRarity.COMMON
    open val type: ItemType = ItemType.NONE
    open val statBonuses: Map<Stat, Double> = emptyMap()
    open val abilities: List<ItemAbility> = emptyList()

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
            customize(this)
        }

        val meta = stack.itemMeta ?: return stack

        meta.persistentDataContainer.set(ITEM_ID_KEY, PersistentDataType.STRING, id)
        meta.persistentDataContainer.set(ITEM_RARITY_KEY, PersistentDataType.STRING, rarity.name)
        meta.persistentDataContainer.set(ITEM_TYPE_KEY, PersistentDataType.STRING, type.name)

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

        suppressVanillaAttributes(meta)
        stack.itemMeta = meta
        return stack
    }

    open fun customize(builder: ItemStackBuilder) {}

    fun matches(stack: ItemStack): Boolean {
        val meta = stack.itemMeta ?: return false
        return meta.persistentDataContainer.get(ITEM_ID_KEY, PersistentDataType.STRING) == id
    }

    fun asChoice(): RecipeChoice.ExactChoice = RecipeChoice.ExactChoice(create(1))

    private fun suppressVanillaAttributes(meta: org.bukkit.inventory.meta.ItemMeta) {
        meta.attributeModifiers = HashMultimap.create()
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
    }
}
