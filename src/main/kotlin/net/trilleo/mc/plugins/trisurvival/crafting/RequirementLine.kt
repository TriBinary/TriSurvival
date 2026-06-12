package net.trilleo.mc.plugins.trisurvival.crafting

/** A single unlock requirement for a recipe, with whether the viewing player currently meets it. */
data class RequirementLine(val text: String, val met: Boolean)
