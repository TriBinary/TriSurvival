package net.trilleo.mc.plugins.trisurvival.items

import com.google.common.collect.HashMultimap
import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.enchants.EnchantBook
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.stats.GearBonusReader
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.persistence.PersistentDataType

object VanillaItemConverter {

    private val mm = MiniMessage.miniMessage()
    private val CONVERTED_KEY = org.bukkit.NamespacedKey.fromString("trisurvival:vanilla_converted")!!

    fun convert(stack: ItemStack): Boolean {
        if (stack.type == Material.AIR || stack.amount == 0) return false
        if (isConverted(stack)) return false
        if (isCustomItem(stack)) return false
        // Enchant books carry their own name/lore/glint; converting would clobber them.
        if (EnchantBook.isBook(stack)) return false

        val profile = VanillaItemConfig.get(stack.type)
            ?: return convertUnregistered(stack)

        applyProfile(stack, profile)
        return true
    }

    fun convertInventory(inventory: PlayerInventory) {
        for (i in 0 until inventory.size) {
            val item = inventory.getItem(i) ?: continue
            if (convert(item)) {
                inventory.setItem(i, item)
            }
        }
    }

    fun isConverted(stack: ItemStack): Boolean {
        val meta = stack.itemMeta ?: return false
        return meta.persistentDataContainer.has(CONVERTED_KEY)
                || meta.persistentDataContainer.has(PluginItem.ITEM_RARITY_KEY)
    }

    private fun isCustomItem(stack: ItemStack): Boolean {
        val meta = stack.itemMeta ?: return false
        return meta.persistentDataContainer.has(PluginItem.ITEM_ID_KEY)
    }

    private fun convertUnregistered(stack: ItemStack): Boolean {
        val rarity = VanillaItemConfig.defaultRarity(stack.type)
        val profile = VanillaItemProfile(rarity)
        applyProfile(stack, profile)
        return true
    }

    private fun applyProfile(stack: ItemStack, profile: VanillaItemProfile) {
        val meta = stack.itemMeta ?: return

        val materialName = formatMaterialName(stack.type)
        val namePrefix = if (profile.rarity.bold) "${profile.rarity.color}<bold>" else profile.rarity.color
        meta.displayName(mm.deserialize("<!i>$namePrefix$materialName"))

        meta.persistentDataContainer.set(CONVERTED_KEY, PersistentDataType.BYTE, 1)
        meta.persistentDataContainer.set(
            PluginItem.ITEM_RARITY_KEY, PersistentDataType.STRING, profile.rarity.name
        )
        meta.persistentDataContainer.set(
            PluginItem.ITEM_TYPE_KEY, PersistentDataType.STRING, profile.type.name
        )

        if (profile.stats.isNotEmpty()) {
            meta.persistentDataContainer.set(
                GearBonusReader.STAT_BONUSES_KEY,
                PersistentDataType.STRING,
                GearBonusReader.encodeBonuses(profile.stats)
            )
        }

        val loreLines = ItemLoreGenerator.generateFromProfile(profile)
        meta.lore(loreLines)

        if (hasVanillaAttributes(stack.type)) {
            meta.attributeModifiers = HashMultimap.create()
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }

        stack.itemMeta = meta
    }

    private fun hasVanillaAttributes(material: Material): Boolean {
        val name = material.name
        return name.endsWith("_SWORD") || name.endsWith("_AXE") || name.endsWith("_PICKAXE")
                || name.endsWith("_SHOVEL") || name.endsWith("_HOE")
                || name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE")
                || name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS")
                || material == Material.TRIDENT || material == Material.MACE
                || material == Material.SHIELD || material == Material.ELYTRA
                || material == Material.TURTLE_HELMET
    }

    private fun formatMaterialName(material: Material): String =
        material.name
            .lowercase()
            .split('_')
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
}
