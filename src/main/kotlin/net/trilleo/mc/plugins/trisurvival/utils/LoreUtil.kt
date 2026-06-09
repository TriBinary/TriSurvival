package net.trilleo.mc.plugins.trisurvival.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage

object LoreUtil {

    private val miniMessage = MiniMessage.miniMessage()

    fun wrapLore(text: String, maxWidth: Int = 40): List<Component> {
        if (text.isEmpty()) return emptyList()

        val normalized = text.replace("<newline>", "\n")
        val segments = normalized.split("\n")

        val result = mutableListOf<Component>()
        var carryOverStyle = Style.empty()

        for (segment in segments) {
            if (segment.isEmpty()) {
                result.add(buildLoreLine(emptyList()))
                continue
            }

            val component = miniMessage.deserialize(segment)
            val styledChars = flattenComponent(component, carryOverStyle)

            val wrappedLines = wrapStyledChars(styledChars, maxWidth)

            for (line in wrappedLines) {
                result.add(buildLoreLine(line))
                if (line.isNotEmpty()) {
                    carryOverStyle = line.last().style
                }
            }

            if (styledChars.isNotEmpty()) {
                carryOverStyle = styledChars.last().style
            }
        }

        return result
    }

    private data class StyledChar(val char: Char, val style: Style)

    private fun flattenComponent(component: Component, parentStyle: Style): List<StyledChar> {
        val result = mutableListOf<StyledChar>()
        val resolvedStyle = parentStyle.merge(component.style(), Style.Merge.Strategy.IF_ABSENT_ON_TARGET)

        if (component is TextComponent) {
            for (char in component.content()) {
                result.add(StyledChar(char, resolvedStyle))
            }
        }

        for (child in component.children()) {
            result.addAll(flattenComponent(child, resolvedStyle))
        }

        return result
    }

    private fun wrapStyledChars(chars: List<StyledChar>, maxWidth: Int): List<List<StyledChar>> {
        if (chars.isEmpty()) return listOf(emptyList())

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

        val lines = mutableListOf<List<StyledChar>>()
        var currentLine = mutableListOf<StyledChar>()

        for (word in words) {
            if (word.isEmpty()) {
                if (currentLine.size < maxWidth) {
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
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine)
                    currentLine = mutableListOf()
                }
                val broken = forceBreakWord(word, maxWidth)
                lines.addAll(broken.dropLast(1))
                currentLine = broken.last().toMutableList()
            } else if (currentLine.isEmpty()) {
                currentLine.addAll(word)
            } else if (currentLine.size + 1 + word.size <= maxWidth) {
                currentLine.add(StyledChar(' ', word.first().style))
                currentLine.addAll(word)
            } else {
                lines.add(currentLine)
                currentLine = word.toMutableList()
            }
        }

        if (currentLine.isNotEmpty() || lines.isEmpty()) {
            lines.add(currentLine)
        }

        return lines
    }

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

    private fun buildLoreLine(chars: List<StyledChar>): Component {
        if (chars.isEmpty()) {
            return Component.empty().style(Style.style().decoration(TextDecoration.ITALIC, false).build())
        }

        val builder = Component.text().style(
            Style.style().decoration(TextDecoration.ITALIC, false).build()
        )

        var currentStyle = chars.first().style
        val buffer = StringBuilder()

        for (sc in chars) {
            if (sc.style == currentStyle) {
                buffer.append(sc.char)
            } else {
                builder.append(Component.text(buffer.toString(), currentStyle))
                buffer.clear()
                currentStyle = sc.style
                buffer.append(sc.char)
            }
        }

        if (buffer.isNotEmpty()) {
            builder.append(Component.text(buffer.toString(), currentStyle))
        }

        return builder.build()
    }
}
