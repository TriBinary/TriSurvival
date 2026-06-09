package com.example.exampleplugin.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoreUtilTest {

    private val plain = PlainTextComponentSerializer.plainText()

    private fun plainText(component: Component): String = plain.serialize(component)

    @Test
    fun `empty string returns empty list`() {
        val result = LoreUtil.wrapLore("")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `short text produces single line`() {
        val result = LoreUtil.wrapLore("Hello world")
        assertEquals(1, result.size)
        assertEquals("Hello world", plainText(result[0]))
    }

    @Test
    fun `long plain text wraps at word boundaries within default 40 chars`() {
        val input = "This is a somewhat longer text that should definitely be wrapped across multiple lines"
        val result = LoreUtil.wrapLore(input)
        assertTrue(result.size > 1, "Expected multiple lines but got ${result.size}")
        for (line in result) {
            assertTrue(plainText(line).length <= 40, "Line exceeds 40 chars: '${plainText(line)}'")
        }
        // Verify all words are preserved
        val reconstructed = result.joinToString(" ") { plainText(it) }
        assertEquals(input, reconstructed)
    }

    @Test
    fun `custom maxWidth is respected`() {
        val input = "Short words in a line"
        val result = LoreUtil.wrapLore(input, maxWidth = 10)
        assertTrue(result.size > 1)
        for (line in result) {
            assertTrue(plainText(line).length <= 10, "Line exceeds 10 chars: '${plainText(line)}'")
        }
    }

    @Test
    fun `MiniMessage tags do not count toward width`() {
        // "<red>" adds no visible characters
        val input = "<red>Short text here"
        val result = LoreUtil.wrapLore(input, maxWidth = 40)
        assertEquals(1, result.size)
        assertEquals("Short text here", plainText(result[0]))
    }

    @Test
    fun `style carries over to next line`() {
        // A red-colored text that wraps should keep red on second line
        val input = "<red>This text is red and long enough to wrap across two lines definitely"
        val result = LoreUtil.wrapLore(input, maxWidth = 30)
        assertTrue(result.size >= 2)

        // Check that the second line's first text child has red color
        val secondLine = result[1]
        val firstChild = findFirstTextChild(secondLine)
        assertTrue(firstChild != null, "Second line should have text content")
        assertEquals(NamedTextColor.RED, firstChild.style().color(), "Style should carry over as red")
    }

    @Test
    fun `explicit newline forces line break`() {
        val input = "Line one\nLine two"
        val result = LoreUtil.wrapLore(input)
        assertEquals(2, result.size)
        assertEquals("Line one", plainText(result[0]))
        assertEquals("Line two", plainText(result[1]))
    }

    @Test
    fun `newline tag forces line break`() {
        val input = "Line one<newline>Line two"
        val result = LoreUtil.wrapLore(input)
        assertEquals(2, result.size)
        assertEquals("Line one", plainText(result[0]))
        assertEquals("Line two", plainText(result[1]))
    }

    @Test
    fun `single long word exceeding maxWidth is force-broken`() {
        val input = "Supercalifragilisticexpialidocious"
        val result = LoreUtil.wrapLore(input, maxWidth = 10)
        assertTrue(result.size > 1, "Long word should be force-broken")
        for (line in result) {
            assertTrue(plainText(line).length <= 10, "Line exceeds 10 chars: '${plainText(line)}'")
        }
        val reconstructed = result.joinToString("") { plainText(it) }
        assertEquals(input, reconstructed)
    }

    @Test
    fun `each line has italic set to false to override default lore style`() {
        val result = LoreUtil.wrapLore("Some lore text")
        for (line in result) {
            val italicState = line.style().decoration(TextDecoration.ITALIC)
            assertEquals(
                TextDecoration.State.FALSE,
                italicState,
                "Each lore line should have italic=false to reset default styling"
            )
        }
    }

    @Test
    fun `multiple spaces between words are handled`() {
        val input = "Hello world"
        val result = LoreUtil.wrapLore(input, maxWidth = 40)
        // The behavior should handle this gracefully (spaces treated as word separators)
        assertEquals(1, result.size)
    }

    /**
     * Finds the first TextComponent with non-empty content in a component tree.
     */
    private fun findFirstTextChild(component: Component): TextComponent? {
        if (component is TextComponent && component.content().isNotEmpty()) {
            return component
        }
        for (child in component.children()) {
            val found = findFirstTextChild(child)
            if (found != null) return found
        }
        return null
    }
}
