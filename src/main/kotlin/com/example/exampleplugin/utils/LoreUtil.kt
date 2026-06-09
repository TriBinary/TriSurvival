package com.example.exampleplugin.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage

/**
 * Utility for wrapping MiniMessage-formatted text into multiple lore-ready
 * [Component] lines with word-aware line breaking, configurable width, and
 * automatic style carry-over.
 *
 * Each output line is prefixed with a style reset (`<!i>`) to neutralize
 * Minecraft's default purple italic lore styling.
 *
 * ### Usage
 *
 * ```kotlin
 * import com.example.exampleplugin.utils.LoreUtil
 *
 * val lines = LoreUtil.wrapLore("<gray>A long description text that will be wrapped.")
 * val narrow = LoreUtil.wrapLore("<red>Warning: dangerous item!", maxWidth = 30)
 * ```
 *
 * Explicit newlines (`\n` or `<newline>`) force a line break:
 * ```kotlin
 * val lines = LoreUtil.wrapLore("<gray>Line one\nLine two")
 * ```
 */
object LoreUtil {

    private val miniMessage = MiniMessage.miniMessage()

    /**
     * Wraps a MiniMessage-formatted string into multiple lore-ready [Component] lines.
     *
     * @param text the MiniMessage-formatted input string
     * @param maxWidth maximum number of visible characters per line (default 40)
     * @return a list of [Component] lines suitable for use as item lore
     */
    fun wrapLore(text: String, maxWidth: Int = 40): List<Component> {
        if (text.isEmpty()) return emptyList()

        // Pre-process: replace <newline> tag with \n for uniform handling
        val normalized = text.replace("<newline>", "\n")

        // Split on explicit newlines; each segment is wrapped independently
        val segments = normalized.split("\n")

        val result = mutableListOf<Component>()
        var carryOverStyle = Style.empty()

        for (segment in segments) {
            if (segment.isEmpty()) {
                result.add(buildLoreLine(emptyList()))
                continue
            }

            // Parse the segment with any carry-over style context
            val component = miniMessage.deserialize(segment)
            val styledChars = flattenComponent(component, carryOverStyle)

            val wrappedLines = wrapStyledChars(styledChars, maxWidth)

            for (line in wrappedLines) {
                result.add(buildLoreLine(line))
                if (line.isNotEmpty()) {
                    carryOverStyle = line.last().style
                }
            }

            // Update carry-over from the last character of this segment
            if (styledChars.isNotEmpty()) {
                carryOverStyle = styledChars.last().style
            }
        }

        return result
    }

    /**
     * Represents a single visible character paired with its resolved style.
     */
    private data class StyledChar(val char: Char, val style: Style)

    /**
     * Recursively flattens a [Component] tree into a list of [StyledChar],
     * resolving style inheritance from parent to child.
     */
    private fun flattenComponent(component: Component, parentStyle: Style): List<StyledChar> {
        val result = mutableListOf<StyledChar>()
        val resolvedStyle = parentStyle.merge(component.style(), Style.Merge.Strategy.IF_ABSENT_ON_TARGET)

        // Extract text content from TextComponent nodes
        if (component is TextComponent) {
            for (char in component.content()) {
                result.add(StyledChar(char, resolvedStyle))
            }
        }

        // Recurse into children
        for (child in component.children()) {
            result.addAll(flattenComponent(child, resolvedStyle))
        }

        return result
    }

    /**
     * Splits a flat list of styled characters into word-aware wrapped lines,
     * each not exceeding [maxWidth] visible characters.
     */
    private fun wrapStyledChars(chars: List<StyledChar>, maxWidth: Int): List<List<StyledChar>> {
        if (chars.isEmpty()) return listOf(emptyList())

        // First, split into words (separated by spaces)
        val words = mutableListOf<List<StyledChar>>()
        var currentWord = mutableListOf<StyledChar>()

        for (sc in chars) {
            if (sc.char == ' ') {
                words.add(currentWord)
                currentWord = mutableListOf()
            } else {
                currentWord.add(sc)
            }
        }
        words.add(currentWord)

        // Now build lines word by word
        val lines = mutableListOf<List<StyledChar>>()
        var currentLine = mutableListOf<StyledChar>()

        for (word in words) {
            if (word.isEmpty()) {
                // Empty word from consecutive spaces — treat as a space
                if (currentLine.size < maxWidth) {
                    // Just add a space if there's room
                    if (currentLine.isNotEmpty()) {
                        currentLine.add(
                            StyledChar(
                                ' ',
                                if (currentLine.isNotEmpty()) currentLine.last().style else Style.empty()
                            )
                        )
                    }
                }
                continue
            }

            if (word.size > maxWidth) {
                // Force-break a long word
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine)
                    currentLine = mutableListOf()
                }
                val broken = forceBreakWord(word, maxWidth)
                lines.addAll(broken.dropLast(1))
                currentLine = broken.last().toMutableList()
            } else if (currentLine.isEmpty()) {
                // First word on the line
                currentLine.addAll(word)
            } else if (currentLine.size + 1 + word.size <= maxWidth) {
                // Word fits with a space
                currentLine.add(StyledChar(' ', word.first().style))
                currentLine.addAll(word)
            } else {
                // Word doesn't fit — start a new line
                lines.add(currentLine)
                currentLine = word.toMutableList()
            }
        }

        if (currentLine.isNotEmpty() || lines.isEmpty()) {
            lines.add(currentLine)
        }

        return lines
    }

    /**
     * Force-breaks a word that exceeds [maxWidth] into multiple chunks.
     */
    private fun forceBreakWord(word: List<StyledChar>, maxWidth: Int): List<List<StyledChar>> {
        val result = mutableListOf<List<StyledChar>>()
        var i = 0
        while (i < word.size) {
            val end = minOf(i + maxWidth, word.size)
            result.add(word.subList(i, end))
            i = end
        }
        if (result.isEmpty()) result.add(emptyList())
        return result
    }

    /**
     * Builds a single lore line [Component] from styled characters,
     * prefixed with a reset to override Minecraft's default lore styling.
     */
    private fun buildLoreLine(chars: List<StyledChar>): Component {
        if (chars.isEmpty()) {
            return Component.empty().style(Style.style().decoration(TextDecoration.ITALIC, false).build())
        }

        // Group consecutive characters with the same style
        val builder = Component.text().style(
            Style.style().decoration(TextDecoration.ITALIC, false).build()
        )

        var currentStyle = chars.first().style
        val buffer = StringBuilder()

        for (sc in chars) {
            if (sc.style == currentStyle) {
                buffer.append(sc.char)
            } else {
                // Flush current group
                builder.append(Component.text(buffer.toString(), currentStyle))
                buffer.clear()
                currentStyle = sc.style
                buffer.append(sc.char)
            }
        }

        // Flush final group
        if (buffer.isNotEmpty()) {
            builder.append(Component.text(buffer.toString(), currentStyle))
        }

        return builder.build()
    }
}
