package net.trilleo.mc.plugins.trisurvival.utils

import com.google.common.collect.HashMultimap
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

/**
 * DSL builder for creating [ItemStack] instances in a concise, readable way.
 *
 * All text (display name and lore lines) is parsed through
 * [MiniMessage](https://docs.advntr.dev/minimessage/index.html), so you can
 * use MiniMessage tags such as `<bold>`, `<red>`, `<gradient>`, etc.
 *
 * Use the top-level [itemStack] function as the entry point:
 * ```kotlin
 * val sword = itemStack(Material.DIAMOND_SWORD) {
 *     name("<bold><gradient:gold:yellow>Excalibur</gradient></bold>")
 *     lore("<gray>A legendary blade", "<gray>Damage: <red>+20")
 *     enchant(Enchantment.SHARPNESS, 5)
 *     unbreakable(true)
 *     flag(ItemFlag.HIDE_ENCHANTS)
 * }
 * ```
 *
 * Use [pdc] to attach [PDCUtil]-managed persistent data to the item while
 * still inside the builder block:
 * ```kotlin
 * val key = NamespacedKey(plugin, "my_key")
 * val item = itemStack(Material.DIAMOND) {
 *     name("<aqua>My Diamond")
 *     pdc(key, PersistentDataType.STRING, "custom_value")
 * }
 * ```
 *
 * For advanced use-cases not covered by the builder API, use the [meta]
 * escape hatch to modify the [ItemMeta] directly:
 * ```kotlin
 * val head = itemStack(Material.PLAYER_HEAD) {
 *     name("<yellow>Custom Head")
 *     meta {
 *         // 'this' is the ItemMeta — call any Paper API method
 *         (this as org.bukkit.inventory.meta.SkullMeta)
 *             .owningPlayer = org.bukkit.Bukkit.getOfflinePlayer("Notch")
 *     }
 * }
 * ```
 */
class ItemStackBuilder(@PublishedApi internal val material: Material) {

    private val miniMessage = MiniMessage.miniMessage()

    @PublishedApi
    internal var itemAmount: Int = 1

    @PublishedApi
    internal var displayName: String? = null

    @PublishedApi
    internal var loreLines: List<String>? = null

    @PublishedApi
    internal var loreComponentLines: List<Component>? = null

    @PublishedApi
    internal var enchantments: MutableMap<Enchantment, Int> = mutableMapOf()

    @PublishedApi
    internal var isUnbreakable: Boolean = false

    @PublishedApi
    internal var isHideTooltip: Boolean = false

    @PublishedApi
    internal var glintOverride: Boolean? = null

    @PublishedApi
    internal var itemFlags: MutableList<ItemFlag> = mutableListOf()

    @PublishedApi
    internal var modelData: Int? = null

    @PublishedApi
    internal var keepVanillaAttributes: Boolean = false

    @PublishedApi
    internal var metaBlock: (ItemMeta.() -> Unit)? = null

    @PublishedApi
    internal val pdcOperations: MutableList<(PersistentDataContainer) -> Unit> = mutableListOf()

    fun name(name: String) {
        this.displayName = name
    }

    fun lore(vararg lines: String) {
        this.loreLines = lines.toList()
    }

    fun loreComponents(lines: List<Component>) {
        this.loreComponentLines = lines
    }

    fun enchant(enchantment: Enchantment, level: Int) {
        this.enchantments[enchantment] = level
    }

    fun unbreakable(value: Boolean) {
        this.isUnbreakable = value
    }

    fun hideTooltip(value: Boolean) {
        this.isHideTooltip = value
    }

    /** Force the enchantment glint on/off without applying a real enchantment. */
    fun glint(value: Boolean) {
        this.glintOverride = value
    }

    fun amount(amount: Int) {
        this.itemAmount = amount
    }

    fun flag(vararg flags: ItemFlag) {
        this.itemFlags.addAll(flags)
    }

    fun customModelData(data: Int) {
        this.modelData = data
    }

    /**
     * Opt out of the default suppression of vanilla attribute tooltips (e.g. the "+7 Attack Damage"
     * lines on gear materials). By default every built item hides those lines so icons and custom
     * gear show only their own name and lore; call this for the rare functional item that should
     * keep its native attributes.
     */
    fun keepVanillaAttributes() {
        this.keepVanillaAttributes = true
    }

    fun <P, C : Any> pdc(key: NamespacedKey, type: PersistentDataType<P, C>, value: C) {
        pdcOperations.add { container -> container.set(key, type, value) }
    }

    fun meta(block: ItemMeta.() -> Unit) {
        this.metaBlock = block
    }

    fun build(): ItemStack {
        val item = ItemStack(material, itemAmount)
        val meta = item.itemMeta ?: return item

        displayName?.let { meta.displayName(miniMessage.deserialize("<!i>$it")) }
        loreComponentLines?.let { meta.lore(it) }
            ?: loreLines?.let { lines -> meta.lore(lines.map { miniMessage.deserialize("<!i>$it") }) }
        enchantments.forEach { (enchant, level) -> meta.addEnchant(enchant, level, true) }
        meta.isUnbreakable = isUnbreakable
        meta.isHideTooltip = isHideTooltip
        glintOverride?.let { meta.setEnchantmentGlintOverride(it) }
        if (itemFlags.isNotEmpty()) meta.addItemFlags(*itemFlags.toTypedArray())
        modelData?.let { meta.setCustomModelData(it) }
        pdcOperations.forEach { it(meta.persistentDataContainer) }
        metaBlock?.invoke(meta)

        // Hide vanilla attribute tooltips by default so gear-material icons (swords, pickaxes,
        // hoes, …) don't leak "+X Attack Damage" lines into menus. Opt out via keepVanillaAttributes().
        if (!keepVanillaAttributes) {
            meta.attributeModifiers = HashMultimap.create()
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }

        item.itemMeta = meta
        return item
    }
}

fun itemStack(material: Material, block: ItemStackBuilder.() -> Unit): ItemStack {
    return ItemStackBuilder(material).apply(block).build()
}
