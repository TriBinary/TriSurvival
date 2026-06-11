package net.trilleo.mc.plugins.trisurvival.items

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.registration.ItemRegistrar
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.stats.GearBonusReader
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.utils.LoreUtil
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object ItemLoreGenerator {

    private val mm = MiniMessage.miniMessage()
    private val noItalic = Style.style().decoration(TextDecoration.ITALIC, false).build()

    fun generate(item: PluginItem): List<Component> {
        val lines = mutableListOf<Component>()

        val statLines = buildStatLines(item.statBonuses)
        if (statLines.isNotEmpty()) {
            lines.add(emptyLine())
            lines.addAll(statLines)
        }

        val abilityLines = buildAbilityLines(item.abilities)
        if (abilityLines.isNotEmpty()) {
            lines.add(emptyLine())
            lines.addAll(abilityLines)
        }

        lines.add(emptyLine())
        lines.add(buildRarityLine(item.rarity, item.type))

        return lines
    }

    fun generateFromProfile(profile: VanillaItemProfile): List<Component> {
        val lines = mutableListOf<Component>()

        val statLines = buildStatLines(profile.stats)
        if (statLines.isNotEmpty()) {
            lines.add(emptyLine())
            lines.addAll(statLines)
        }

        lines.add(emptyLine())
        lines.add(buildRarityLine(profile.rarity, profile.type))

        return lines
    }

    fun refreshLore(stack: ItemStack) {
        val meta = stack.itemMeta ?: return
        val pdc = meta.persistentDataContainer

        val rarityName = pdc.get(PluginItem.ITEM_RARITY_KEY, PersistentDataType.STRING) ?: return
        val typeName = pdc.get(PluginItem.ITEM_TYPE_KEY, PersistentDataType.STRING) ?: return

        val rarity = runCatching { ItemRarity.valueOf(rarityName) }.getOrNull() ?: return
        val type = runCatching { ItemType.valueOf(typeName) }.getOrNull() ?: return
        val bonuses = GearBonusReader.parseBonuses(stack)

        val itemId = pdc.get(PluginItem.ITEM_ID_KEY, PersistentDataType.STRING)
        val registeredItem = itemId?.let { ItemRegistrar.get(it) }
        val abilities = registeredItem?.abilities ?: emptyList()

        val lines = mutableListOf<Component>()

        val statLines = buildStatLines(bonuses)
        if (statLines.isNotEmpty()) {
            lines.add(emptyLine())
            lines.addAll(statLines)
        }

        val abilityLines = buildAbilityLines(abilities)
        if (abilityLines.isNotEmpty()) {
            lines.add(emptyLine())
            lines.addAll(abilityLines)
        }

        lines.add(emptyLine())
        lines.add(buildRarityLine(rarity, type))

        meta.lore(lines)
        stack.itemMeta = meta
    }

    private fun buildStatLines(bonuses: Map<Stat, Double>): List<Component> {
        if (bonuses.isEmpty()) return emptyList()

        return bonuses.entries
            .sortedBy { it.key.ordinal }
            .map { (stat, value) ->
                val sign = if (value >= 0) "+" else ""
                val suffix = if (stat.isPercentage) "%" else ""
                val valueColor = if (value >= 0) "<green>" else "<red>"
                val display = formatStatValue(value)
                parseLine("${stat.color}${stat.symbol} ${stat.displayName}: $valueColor$sign$display$suffix")
            }
    }

    private fun buildAbilityLines(abilities: List<ItemAbility>): List<Component> {
        if (abilities.isEmpty()) return emptyList()

        val lines = mutableListOf<Component>()
        for (ability in abilities) {
            lines.add(parseLine("<gold><bold>Item Ability: ${ability.name}"))

            if (ability.trigger != AbilityTrigger.PASSIVE) {
                lines.add(parseLine("<yellow><bold>${ability.trigger.displayName}"))
            }

            lines.addAll(LoreUtil.wrapLore("<gray>${ability.description}"))

            if (ability.manaCost > 0) {
                lines.add(parseLine("<dark_aqua>Mana Cost: <dark_aqua>${ability.manaCost}"))
            }

            if (ability.cooldownTicks > 0) {
                val seconds = ability.cooldownTicks / 20.0
                lines.add(parseLine("<dark_aqua>Cooldown: <green>${formatCooldown(seconds)}"))
            }
        }
        return lines
    }

    private fun buildRarityLine(rarity: ItemRarity, type: ItemType): Component {
        val typeSuffix = if (type != ItemType.NONE) " ${type.displayName}" else ""
        val boldTag = if (rarity.bold) "<bold>" else ""
        return parseLine("${rarity.color}$boldTag${rarity.displayName}$typeSuffix")
    }

    private fun parseLine(miniMsg: String): Component =
        mm.deserialize("<reset><i:false>$miniMsg").style(noItalic)

    private fun emptyLine(): Component =
        Component.empty().style(noItalic)

    private fun formatStatValue(value: Double): String =
        if (value == value.toLong().toDouble()) value.toLong().toString() else "%.1f".format(value)

    private fun formatCooldown(seconds: Double): String =
        if (seconds == seconds.toLong().toDouble()) "${seconds.toLong()}s" else "%.1fs".format(seconds)
}
