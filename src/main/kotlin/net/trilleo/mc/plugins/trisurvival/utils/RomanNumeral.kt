package net.trilleo.mc.plugins.trisurvival.utils

object RomanNumeral {

    private val table = listOf(
        1000 to "M", 900 to "CM", 500 to "D", 400 to "CD",
        100 to "C", 90 to "XC", 50 to "L", 40 to "XL",
        10 to "X", 9 to "IX", 5 to "V", 4 to "IV", 1 to "I"
    )

    fun toRoman(value: Int): String {
        if (value <= 0) return value.toString()
        var remaining = value
        val sb = StringBuilder()
        for ((number, numeral) in table) {
            while (remaining >= number) {
                sb.append(numeral)
                remaining -= number
            }
        }
        return sb.toString()
    }
}
