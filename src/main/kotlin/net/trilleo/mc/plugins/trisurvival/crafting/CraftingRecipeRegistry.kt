package net.trilleo.mc.plugins.trisurvival.crafting

import net.trilleo.mc.plugins.trisurvival.crafting.transfer.AttributeTransferEngine
import net.trilleo.mc.plugins.trisurvival.registration.PackageScanner
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.plugin.java.JavaPlugin

object CraftingRecipeRegistry {

    private const val CUSTOM_RECIPES_PACKAGE = "net.trilleo.mc.plugins.trisurvival.recipes.custom"

    private val vanillaShaped = mutableListOf<ShapedRecipe>()
    private val vanillaShapeless = mutableListOf<ShapelessRecipe>()
    private val customRecipes = mutableListOf<CustomRecipe>()

    fun init(plugin: JavaPlugin) {
        vanillaShaped.clear()
        vanillaShapeless.clear()
        customRecipes.clear()

        val iter = Bukkit.recipeIterator()
        while (iter.hasNext()) {
            when (val recipe = iter.next()) {
                is ShapedRecipe -> vanillaShaped.add(recipe)
                is ShapelessRecipe -> vanillaShapeless.add(recipe)
            }
        }

        val customClasses = PackageScanner.findClasses(plugin, CUSTOM_RECIPES_PACKAGE, CustomRecipe::class.java)
        for (clazz in customClasses) {
            try {
                val instance = resolveInstance(clazz, plugin)
                require(instance.shape.size == 9) {
                    "CustomRecipe ${instance.key} shape must have exactly 9 entries"
                }
                customRecipes.add(instance)
                plugin.logger.info("Registered custom crafting recipe: ${instance.key}")
            } catch (e: Exception) {
                plugin.logger.severe("Failed to register custom recipe ${clazz.simpleName}: ${e.message}")
            }
        }

        plugin.logger.info(
            "Crafting registry loaded: ${vanillaShaped.size} vanilla shaped, " +
                    "${vanillaShapeless.size} vanilla shapeless, ${customRecipes.size} custom"
        )
    }

    fun findMatch(grid: Array<ItemStack?>): CraftingMatch? {
        require(grid.size == 9)
        return matchCustomRecipes(grid) ?: matchVanillaShaped(grid) ?: matchVanillaShapeless(grid)
    }

    /** All registered custom recipes (for the recipe book). */
    fun customRecipes(): List<CustomRecipe> = customRecipes.toList()

    /** Custom recipes filed under the given [category]. */
    fun customRecipesByCategory(category: RecipeCategory): List<CustomRecipe> =
        customRecipes.filter { it.category == category }

    /** Looks up a custom recipe by its [CustomRecipe.key]. */
    fun customRecipe(key: String): CustomRecipe? = customRecipes.firstOrNull { it.key == key }

    /**
     * Builds the [CraftingMatch] result for a matched custom recipe, applying any
     * declared attribute transfer so both the live preview and the actual craft
     * inherit the transferred attributes.
     */
    private fun buildCustomMatch(
        recipe: CustomRecipe,
        grid: Array<ItemStack?>,
        consumeAmounts: IntArray
    ): CraftingMatch {
        val result = recipe.result.clone()
        recipe.transfer?.let { AttributeTransferEngine.apply(it, grid, result) }
        return CraftingMatch(result, consumeAmounts)
    }

    // --- Custom recipe matching ---

    private fun matchCustomRecipes(grid: Array<ItemStack?>): CraftingMatch? {
        for (recipe in customRecipes) {
            val match = if (recipe.shaped) matchCustomShaped(recipe, grid) else matchCustomShapeless(recipe, grid)
            if (match != null) return match
        }
        return null
    }

    private fun matchCustomShaped(recipe: CustomRecipe, grid: Array<ItemStack?>): CraftingMatch? {
        val normalizedGrid = normalizeGrid(grid.map { it != null })
        val normalizedRecipe = normalizeGrid(recipe.shape.map { it != null })

        if (normalizedGrid.width != normalizedRecipe.width || normalizedGrid.height != normalizedRecipe.height) return null

        val consumeAmounts = IntArray(9)

        for (row in 0 until normalizedGrid.height) {
            for (col in 0 until normalizedGrid.width) {
                val gridRow = normalizedGrid.topRow + row
                val gridCol = normalizedGrid.leftCol + col
                val recipeRow = normalizedRecipe.topRow + row
                val recipeCol = normalizedRecipe.leftCol + col

                val gridIdx = gridRow * 3 + gridCol
                val recipeIdx = recipeRow * 3 + recipeCol

                val ingredient = recipe.shape[recipeIdx]
                val stack = grid[gridIdx]

                if (ingredient == null && stack == null) continue
                if (ingredient == null || stack == null) return null
                if (!ingredient.matches(stack)) return null
                if (stack.amount < ingredient.amount) return null

                consumeAmounts[gridIdx] = ingredient.amount
            }
        }

        // Ensure grid slots outside the bounding box are empty
        for (i in 0 until 9) {
            val row = i / 3
            val col = i % 3
            val inBounds = row >= normalizedGrid.topRow && row < normalizedGrid.topRow + normalizedGrid.height &&
                    col >= normalizedGrid.leftCol && col < normalizedGrid.leftCol + normalizedGrid.width
            if (!inBounds && grid[i] != null) return null
        }

        return buildCustomMatch(recipe, grid, consumeAmounts)
    }

    private fun matchCustomShapeless(recipe: CustomRecipe, grid: Array<ItemStack?>): CraftingMatch? {
        val ingredients = recipe.shape.filterNotNull().toMutableList()
        val stacks = grid.withIndex().filter { it.value != null }

        if (ingredients.size != stacks.size) return null

        val consumeAmounts = IntArray(9)
        val used = BooleanArray(ingredients.size)

        for ((gridIdx, stack) in stacks) {
            var matched = false
            for (i in ingredients.indices) {
                if (used[i]) continue
                if (ingredients[i].matches(stack!!) && stack.amount >= ingredients[i].amount) {
                    used[i] = true
                    consumeAmounts[gridIdx] = ingredients[i].amount
                    matched = true
                    break
                }
            }
            if (!matched) return null
        }

        return if (used.all { it }) buildCustomMatch(recipe, grid, consumeAmounts) else null
    }

    // --- Vanilla shaped recipe matching ---

    private fun matchVanillaShaped(grid: Array<ItemStack?>): CraftingMatch? {
        for (recipe in vanillaShaped) {
            val match = tryMatchVanillaShaped(recipe, grid, mirrored = false)
                ?: tryMatchVanillaShaped(recipe, grid, mirrored = true)
            if (match != null) return match
        }
        return null
    }

    private fun tryMatchVanillaShaped(
        recipe: ShapedRecipe,
        grid: Array<ItemStack?>,
        mirrored: Boolean
    ): CraftingMatch? {
        val choiceMap: Map<Char, RecipeChoice> = recipe.choiceMap
        val recipeShape = recipe.shape

        val recipeHeight = recipeShape.size
        val recipeWidth = recipeShape.maxOf { it.length }

        val recipeGrid = Array<RecipeChoice?>(9) { null }
        for (row in 0 until recipeHeight) {
            for (col in 0 until recipeWidth) {
                if (col >= recipeShape[row].length) continue
                val c = recipeShape[row][col]
                if (c == ' ') continue
                val actualCol = if (mirrored) (recipeWidth - 1 - col) else col
                recipeGrid[row * 3 + actualCol] = choiceMap[c]
            }
        }

        val normalizedGrid = normalizeGrid(grid.map { it != null })
        val normalizedRecipe = normalizeGrid(recipeGrid.map { it != null })

        if (normalizedGrid.width != normalizedRecipe.width || normalizedGrid.height != normalizedRecipe.height) return null

        val consumeAmounts = IntArray(9)

        for (row in 0 until normalizedGrid.height) {
            for (col in 0 until normalizedGrid.width) {
                val gridRow = normalizedGrid.topRow + row
                val gridCol = normalizedGrid.leftCol + col
                val recipeRow = normalizedRecipe.topRow + row
                val recipeCol = normalizedRecipe.leftCol + col

                val gridIdx = gridRow * 3 + gridCol
                val recipeIdx = recipeRow * 3 + recipeCol

                val choice = recipeGrid[recipeIdx]
                val stack = grid[gridIdx]

                if (choice == null && stack == null) continue
                if (choice == null || stack == null) return null
                if (!choice.test(stack)) return null

                consumeAmounts[gridIdx] = 1
            }
        }

        for (i in 0 until 9) {
            val row = i / 3
            val col = i % 3
            val inBounds = row >= normalizedGrid.topRow && row < normalizedGrid.topRow + normalizedGrid.height &&
                    col >= normalizedGrid.leftCol && col < normalizedGrid.leftCol + normalizedGrid.width
            if (!inBounds && grid[i] != null) return null
        }

        return CraftingMatch(recipe.result.clone(), consumeAmounts)
    }

    // --- Vanilla shapeless recipe matching ---

    private fun matchVanillaShapeless(grid: Array<ItemStack?>): CraftingMatch? {
        for (recipe in vanillaShapeless) {
            val match = tryMatchVanillaShapeless(recipe, grid)
            if (match != null) return match
        }
        return null
    }

    private fun tryMatchVanillaShapeless(recipe: ShapelessRecipe, grid: Array<ItemStack?>): CraftingMatch? {
        val choices = recipe.choiceList.toMutableList()
        val stacks = grid.withIndex().filter { it.value != null }

        if (choices.size != stacks.size) return null

        val consumeAmounts = IntArray(9)
        val used = BooleanArray(choices.size)

        for ((gridIdx, stack) in stacks) {
            var matched = false
            for (i in choices.indices) {
                if (used[i]) continue
                if (choices[i].test(stack!!)) {
                    used[i] = true
                    consumeAmounts[gridIdx] = 1
                    matched = true
                    break
                }
            }
            if (!matched) return null
        }

        return if (used.all { it }) CraftingMatch(recipe.result.clone(), consumeAmounts) else null
    }

    // --- Grid normalization ---

    private data class BoundingBox(val topRow: Int, val leftCol: Int, val width: Int, val height: Int)

    private fun normalizeGrid(occupied: List<Boolean>): BoundingBox {
        var minRow = 3;
        var maxRow = -1;
        var minCol = 3;
        var maxCol = -1
        for (i in occupied.indices) {
            if (!occupied[i]) continue
            val row = i / 3
            val col = i % 3
            if (row < minRow) minRow = row
            if (row > maxRow) maxRow = row
            if (col < minCol) minCol = col
            if (col > maxCol) maxCol = col
        }
        if (maxRow == -1) return BoundingBox(0, 0, 0, 0)
        return BoundingBox(minRow, minCol, maxCol - minCol + 1, maxRow - minRow + 1)
    }

    // --- Instance resolution ---

    private fun resolveInstance(clazz: Class<out CustomRecipe>, plugin: JavaPlugin): CustomRecipe {
        try {
            val field = clazz.getDeclaredField("INSTANCE")
            if (field.trySetAccessible()) {
                return field.get(null) as CustomRecipe
            }
        } catch (_: NoSuchFieldException) {
        }

        return try {
            clazz.getDeclaredConstructor(JavaPlugin::class.java).newInstance(plugin)
        } catch (_: NoSuchMethodException) {
            try {
                clazz.getDeclaredConstructor().newInstance()
            } catch (_: NoSuchMethodException) {
                throw IllegalArgumentException(
                    "${clazz.simpleName} must be a Kotlin object, or declare either a no-arg constructor " +
                            "or a constructor accepting a single JavaPlugin parameter"
                )
            }
        }
    }
}
