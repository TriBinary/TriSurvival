package net.trilleo.mc.plugins.trisurvival.items

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.trilleo.mc.plugins.trisurvival.enchants.AbilityEnchant
import net.trilleo.mc.plugins.trisurvival.enchants.CustomEnchant
import net.trilleo.mc.plugins.trisurvival.enchants.EnchantBonusReader
import net.trilleo.mc.plugins.trisurvival.enchants.EnchantData
import net.trilleo.mc.plugins.trisurvival.registration.ItemRegistrar
import net.trilleo.mc.plugins.trisurvival.registration.PluginItem
import net.trilleo.mc.plugins.trisurvival.stats.GearBonusReader
import net.trilleo.mc.plugins.trisurvival.stats.Stat
import net.trilleo.mc.plugins.trisurvival.utils.LoreUtil
import net.trilleo.mc.plugins.trisurvival.utils.RomanNumeral
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object ItemLoreGenerator {

    private val mm = MiniMessage.miniMessage()

    fun generate(item: PluginItem): List<Component> {
        val lines = mutableListOf<Component>()

        val statLines = buildStatLines(item.statBonuses, emptyMap())
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

        val statLines = buildStatLines(profile.stats, emptyMap())
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
        val enchantBonuses = EnchantBonusReader.enchantStatBonuses(stack)
        val enchants = EnchantData.read(stack)

        val itemId = pdc.get(PluginItem.ITEM_ID_KEY, PersistentDataType.STRING)
        val registeredItem = itemId?.let { ItemRegistrar.get(it) }
        val abilities = registeredItem?.abilities ?: emptyList()

        val lines = mutableListOf<Component>()

        val statLines = buildStatLines(bonuses, enchantBonuses)
        if (statLines.isNotEmpty()) {
            lines.add(emptyLine())
            lines.addAll(statLines)
        }

        val enchantLines = buildEnchantLines(enchants)
        if (enchantLines.isNotEmpty()) {
            lines.add(emptyLine())
            lines.addAll(enchantLines)
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

    /**
     * Renders one line per stat. The item's intrinsic bonus is shown in the
     * stat's own colour; any enchant-derived bonus is appended in magenta:
     * `Damage: +20 +30` (the second value being the enchant contribution).
     */
    private fun buildStatLines(
        intrinsic: Map<Stat, Double>,
        enchant: Map<Stat, Double>
    ): List<Component> {
        if (intrinsic.isEmpty() && enchant.isEmpty()) return emptyList()

        return (intrinsic.keys + enchant.keys)
            .distinct()
            .sortedBy { it.ordinal }
            .map { stat ->
                val suffix = if (stat.isPercentage) "%" else ""
                val parts = mutableListOf<String>()
                intrinsic[stat]?.let {
                    val sign = if (it >= 0) "+" else ""
                    parts.add("${stat.color}$sign${formatStatValue(it)}$suffix")
                }
                enchant[stat]?.let {
                    val sign = if (it >= 0) "+" else ""
                    parts.add("<light_purple>$sign${formatStatValue(it)}$suffix")
                }
                parseLine("<gray>${stat.displayName}: ${parts.joinToString(" ")}")
            }
    }

    private fun buildEnchantLines(enchants: Map<CustomEnchant, Int>): List<Component> {
        if (enchants.isEmpty()) return emptyList()

        val lines = mutableListOf<Component>()
        val sorted = enchants.entries.sortedWith(
            compareByDescending<Map.Entry<CustomEnchant, Int>> { it.key.ultimate }
                .thenBy { it.key.displayName }
        )
        for ((enchant, level) in sorted) {
            val color = if (enchant.ultimate) "<light_purple><bold>" else "<blue>"
            lines.add(parseLine("$color${enchant.displayName} ${RomanNumeral.toRoman(level)}"))
            if (enchant is AbilityEnchant) {
                lines.addAll(LoreUtil.wrapLore("<gray>${enchant.description(level)}"))
            }
        }
        return lines
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
        mm.deserialize("<!i>$miniMsg")

    private fun emptyLine(): Component =
        Component.empty().decoration(TextDecoration.ITALIC, false)

    private fun formatStatValue(value: Double): String =
        if (value == value.toLong().toDouble()) value.toLong().toString() else "%.1f".format(value)

    private fun formatCooldown(seconds: Double): String =
        if (seconds == seconds.toLong().toDouble()) "${seconds.toLong()}s" else "%.1fs".format(seconds)
}
