package net.trilleo.mc.plugins.trisurvival.enchants

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.utils.LoreUtil
import net.trilleo.mc.plugins.trisurvival.utils.PDCUtil
import net.trilleo.mc.plugins.trisurvival.utils.RomanNumeral
import net.trilleo.mc.plugins.trisurvival.utils.itemStack
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Factory and reader for enchanted books. A book carries exactly one enchant at
 * one level, stored in PDC as `"{enchantId}:{level}"`, and is applied to gear or
 * combined with another book at the custom anvil.
 */
object EnchantBook {

    @JvmField
    val BOOK_KEY: NamespacedKey = NamespacedKey.fromString("trisurvival:enchant_book")!!

    private val mm = MiniMessage.miniMessage()

    fun create(enchant: CustomEnchant, level: Int, amount: Int = 1): ItemStack {
        val color = if (enchant.ultimate) "<light_purple><bold>" else "<blue>"
        val title = "$color${enchant.displayName} ${RomanNumeral.toRoman(level)}"

        val lore = mutableListOf<Component>(emptyLine())
        enchant.description(level).takeIf { it.isNotBlank() }?.let {
            lore.addAll(LoreUtil.wrapLore("<gray>$it"))
        }
        lore.add(emptyLine())
        lore.add(mm.deserialize("<!i><dark_gray>Use an anvil to apply"))

        return itemStack(Material.ENCHANTED_BOOK) {
            amount(amount)
            name(title)
            loreComponents(lore)
            meta { setEnchantmentGlintOverride(true) }
            pdc(BOOK_KEY, PersistentDataType.STRING, "${enchant.id}:$level")
        }
    }

    fun read(item: ItemStack): Pair<CustomEnchant, Int>? {
        if (item.type != Material.ENCHANTED_BOOK) return null
        val raw = PDCUtil.get(item, BOOK_KEY, PersistentDataType.STRING) ?: return null
        val idx = raw.lastIndexOf(':')
        if (idx <= 0) return null
        val id = raw.substring(0, idx)
        val level = raw.substring(idx + 1).toIntOrNull() ?: return null
        val enchant = EnchantRegistry.get(id) ?: return null
        return enchant to level
    }

    fun isBook(item: ItemStack): Boolean = read(item) != null

    private fun emptyLine(): Component =
        Component.empty().decoration(TextDecoration.ITALIC, false)
}
