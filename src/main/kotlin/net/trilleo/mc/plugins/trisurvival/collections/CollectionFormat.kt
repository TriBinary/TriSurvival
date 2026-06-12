package net.trilleo.mc.plugins.trisurvival.collections

/** Small formatting helpers shared by the collection UI and messages. */
object CollectionFormat {

    private val numerals = listOf(
        1000 to "M", 900 to "CM", 500 to "D", 400 to "CD",
        100 to "C", 90 to "XC", 50 to "L", 40 to "XL",
        10 to "X", 9 to "IX", 5 to "V", 4 to "IV", 1 to "I"
    )

    /** Renders [value] as a Roman numeral (returns "0" for non-positive values). */
    fun roman(value: Int): String {
        if (value <= 0) return "0"
        var remaining = value
        val sb = StringBuilder()
        for ((amount, symbol) in numerals) {
            while (remaining >= amount) {
                sb.append(symbol)
                remaining -= amount
            }
        }
        return sb.toString()
    }
}
