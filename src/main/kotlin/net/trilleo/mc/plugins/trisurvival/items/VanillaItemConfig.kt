package net.trilleo.mc.plugins.trisurvival.items

import net.trilleo.mc.plugins.trisurvival.stats.Stat
import org.bukkit.Material

data class VanillaItemProfile(
    val rarity: ItemRarity,
    val type: ItemType = ItemType.NONE,
    val stats: Map<Stat, Double> = emptyMap()
)

object VanillaItemConfig {

    private val profiles = mutableMapOf<Material, VanillaItemProfile>()

    init {
        registerWeapons()
        registerArmor()
        registerTools()
        registerBows()
        registerMiscGear()
        registerBlocks()
        registerMaterials()
    }

    fun get(material: Material): VanillaItemProfile? = profiles[material]

    fun hasProfile(material: Material): Boolean = material in profiles

    fun defaultRarity(material: Material): ItemRarity = when {
        material.isEdible -> ItemRarity.COMMON
        material.isBlock -> ItemRarity.COMMON
        else -> ItemRarity.COMMON
    }

    private fun register(material: Material, profile: VanillaItemProfile) {
        profiles[material] = profile
    }

    // region Weapons

    private fun registerWeapons() {
        // Swords
        register(Material.WOODEN_SWORD, VanillaItemProfile(
            ItemRarity.COMMON, ItemType.SWORD,
            mapOf(Stat.DAMAGE to 20.0)
        ))
        register(Material.STONE_SWORD, VanillaItemProfile(
            ItemRarity.COMMON, ItemType.SWORD,
            mapOf(Stat.DAMAGE to 25.0)
        ))
        register(Material.GOLDEN_SWORD, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.SWORD,
            mapOf(Stat.DAMAGE to 20.0, Stat.ATTACK_SPEED to 5.0)
        ))
        register(Material.IRON_SWORD, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.SWORD,
            mapOf(Stat.DAMAGE to 30.0)
        ))
        register(Material.DIAMOND_SWORD, VanillaItemProfile(
            ItemRarity.RARE, ItemType.SWORD,
            mapOf(Stat.DAMAGE to 35.0, Stat.STRENGTH to 10.0)
        ))
        register(Material.NETHERITE_SWORD, VanillaItemProfile(
            ItemRarity.EPIC, ItemType.SWORD,
            mapOf(Stat.DAMAGE to 50.0, Stat.STRENGTH to 20.0, Stat.FEROCITY to 5.0)
        ))
        register(Material.TRIDENT, VanillaItemProfile(
            ItemRarity.RARE, ItemType.SWORD,
            mapOf(Stat.DAMAGE to 40.0, Stat.STRENGTH to 15.0)
        ))
        register(Material.MACE, VanillaItemProfile(
            ItemRarity.EPIC, ItemType.SWORD,
            mapOf(Stat.DAMAGE to 45.0, Stat.STRENGTH to 25.0, Stat.CRIT_DAMAGE to 10.0)
        ))
    }

    // endregion

    // region Armor

    private fun registerArmor() {
        // Leather
        register(Material.LEATHER_HELMET, VanillaItemProfile(
            ItemRarity.COMMON, ItemType.HELMET,
            mapOf(Stat.HEALTH to 5.0, Stat.DEFENSE to 5.0)
        ))
        register(Material.LEATHER_CHESTPLATE, VanillaItemProfile(
            ItemRarity.COMMON, ItemType.CHESTPLATE,
            mapOf(Stat.HEALTH to 10.0, Stat.DEFENSE to 15.0)
        ))
        register(Material.LEATHER_LEGGINGS, VanillaItemProfile(
            ItemRarity.COMMON, ItemType.LEGGINGS,
            mapOf(Stat.HEALTH to 7.0, Stat.DEFENSE to 10.0)
        ))
        register(Material.LEATHER_BOOTS, VanillaItemProfile(
            ItemRarity.COMMON, ItemType.BOOTS,
            mapOf(Stat.HEALTH to 3.0, Stat.DEFENSE to 5.0, Stat.SPEED to 2.0)
        ))

        // Chainmail
        register(Material.CHAINMAIL_HELMET, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.HELMET,
            mapOf(Stat.HEALTH to 10.0, Stat.DEFENSE to 12.0)
        ))
        register(Material.CHAINMAIL_CHESTPLATE, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.CHESTPLATE,
            mapOf(Stat.HEALTH to 15.0, Stat.DEFENSE to 25.0)
        ))
        register(Material.CHAINMAIL_LEGGINGS, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.LEGGINGS,
            mapOf(Stat.HEALTH to 12.0, Stat.DEFENSE to 20.0)
        ))
        register(Material.CHAINMAIL_BOOTS, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.BOOTS,
            mapOf(Stat.HEALTH to 5.0, Stat.DEFENSE to 10.0, Stat.SPEED to 3.0)
        ))

        // Iron
        register(Material.IRON_HELMET, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.HELMET,
            mapOf(Stat.HEALTH to 15.0, Stat.DEFENSE to 20.0)
        ))
        register(Material.IRON_CHESTPLATE, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.CHESTPLATE,
            mapOf(Stat.HEALTH to 25.0, Stat.DEFENSE to 40.0)
        ))
        register(Material.IRON_LEGGINGS, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.LEGGINGS,
            mapOf(Stat.HEALTH to 20.0, Stat.DEFENSE to 30.0)
        ))
        register(Material.IRON_BOOTS, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.BOOTS,
            mapOf(Stat.HEALTH to 10.0, Stat.DEFENSE to 15.0, Stat.SPEED to 4.0)
        ))

        // Gold
        register(Material.GOLDEN_HELMET, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.HELMET,
            mapOf(Stat.HEALTH to 10.0, Stat.DEFENSE to 10.0, Stat.INTELLIGENCE to 10.0)
        ))
        register(Material.GOLDEN_CHESTPLATE, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.CHESTPLATE,
            mapOf(Stat.HEALTH to 15.0, Stat.DEFENSE to 20.0, Stat.INTELLIGENCE to 15.0)
        ))
        register(Material.GOLDEN_LEGGINGS, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.LEGGINGS,
            mapOf(Stat.HEALTH to 12.0, Stat.DEFENSE to 15.0, Stat.INTELLIGENCE to 12.0)
        ))
        register(Material.GOLDEN_BOOTS, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.BOOTS,
            mapOf(Stat.HEALTH to 5.0, Stat.DEFENSE to 8.0, Stat.SPEED to 5.0, Stat.INTELLIGENCE to 8.0)
        ))

        // Diamond
        register(Material.DIAMOND_HELMET, VanillaItemProfile(
            ItemRarity.RARE, ItemType.HELMET,
            mapOf(Stat.HEALTH to 25.0, Stat.DEFENSE to 35.0)
        ))
        register(Material.DIAMOND_CHESTPLATE, VanillaItemProfile(
            ItemRarity.RARE, ItemType.CHESTPLATE,
            mapOf(Stat.HEALTH to 40.0, Stat.DEFENSE to 60.0)
        ))
        register(Material.DIAMOND_LEGGINGS, VanillaItemProfile(
            ItemRarity.RARE, ItemType.LEGGINGS,
            mapOf(Stat.HEALTH to 30.0, Stat.DEFENSE to 50.0)
        ))
        register(Material.DIAMOND_BOOTS, VanillaItemProfile(
            ItemRarity.RARE, ItemType.BOOTS,
            mapOf(Stat.HEALTH to 15.0, Stat.DEFENSE to 25.0, Stat.SPEED to 6.0)
        ))

        // Netherite
        register(Material.NETHERITE_HELMET, VanillaItemProfile(
            ItemRarity.EPIC, ItemType.HELMET,
            mapOf(Stat.HEALTH to 35.0, Stat.DEFENSE to 50.0, Stat.STRENGTH to 5.0)
        ))
        register(Material.NETHERITE_CHESTPLATE, VanillaItemProfile(
            ItemRarity.EPIC, ItemType.CHESTPLATE,
            mapOf(Stat.HEALTH to 55.0, Stat.DEFENSE to 80.0, Stat.STRENGTH to 10.0)
        ))
        register(Material.NETHERITE_LEGGINGS, VanillaItemProfile(
            ItemRarity.EPIC, ItemType.LEGGINGS,
            mapOf(Stat.HEALTH to 45.0, Stat.DEFENSE to 65.0, Stat.STRENGTH to 7.0)
        ))
        register(Material.NETHERITE_BOOTS, VanillaItemProfile(
            ItemRarity.EPIC, ItemType.BOOTS,
            mapOf(Stat.HEALTH to 20.0, Stat.DEFENSE to 35.0, Stat.SPEED to 8.0, Stat.STRENGTH to 3.0)
        ))

        // Turtle
        register(Material.TURTLE_HELMET, VanillaItemProfile(
            ItemRarity.RARE, ItemType.HELMET,
            mapOf(Stat.HEALTH to 20.0, Stat.DEFENSE to 25.0, Stat.RESPIRATION to 10.0)
        ))

        // Elytra
        register(Material.ELYTRA, VanillaItemProfile(
            ItemRarity.LEGENDARY, ItemType.CHESTPLATE,
            mapOf(Stat.SPEED to 50.0)
        ))
    }

    // endregion

    // region Tools

    private fun registerTools() {
        // Pickaxes
        register(Material.WOODEN_PICKAXE, VanillaItemProfile(
            ItemRarity.COMMON, ItemType.PICKAXE,
            mapOf(Stat.DAMAGE to 10.0, Stat.MINING_SPEED to 10.0)
        ))
        register(Material.STONE_PICKAXE, VanillaItemProfile(
            ItemRarity.COMMON, ItemType.PICKAXE,
            mapOf(Stat.DAMAGE to 15.0, Stat.MINING_SPEED to 25.0)
        ))
        register(Material.GOLDEN_PICKAXE, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.PICKAXE,
            mapOf(Stat.DAMAGE to 10.0, Stat.MINING_SPEED to 50.0, Stat.MINING_FORTUNE to 10.0)
        ))
        register(Material.IRON_PICKAXE, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.PICKAXE,
            mapOf(Stat.DAMAGE to 20.0, Stat.MINING_SPEED to 40.0)
        ))
        register(Material.DIAMOND_PICKAXE, VanillaItemProfile(
            ItemRarity.RARE, ItemType.PICKAXE,
            mapOf(Stat.DAMAGE to 25.0, Stat.MINING_SPEED to 70.0, Stat.MINING_FORTUNE to 5.0)
        ))
        register(Material.NETHERITE_PICKAXE, VanillaItemProfile(
            ItemRarity.EPIC, ItemType.PICKAXE,
            mapOf(Stat.DAMAGE to 30.0, Stat.MINING_SPEED to 100.0, Stat.MINING_FORTUNE to 10.0)
        ))

        // Axes
        register(Material.WOODEN_AXE, VanillaItemProfile(
            ItemRarity.COMMON, ItemType.AXE,
            mapOf(Stat.DAMAGE to 15.0, Stat.FORAGING_FORTUNE to 5.0)
        ))
        register(Material.STONE_AXE, VanillaItemProfile(
            ItemRarity.COMMON, ItemType.AXE,
            mapOf(Stat.DAMAGE to 20.0, Stat.FORAGING_FORTUNE to 10.0)
        ))
        register(Material.GOLDEN_AXE, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.AXE,
            mapOf(Stat.DAMAGE to 15.0, Stat.FORAGING_FORTUNE to 20.0)
        ))
        register(Material.IRON_AXE, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.AXE,
            mapOf(Stat.DAMAGE to 25.0, Stat.FORAGING_FORTUNE to 15.0)
        ))
        register(Material.DIAMOND_AXE, VanillaItemProfile(
            ItemRarity.RARE, ItemType.AXE,
            mapOf(Stat.DAMAGE to 30.0, Stat.FORAGING_FORTUNE to 20.0, Stat.SWEEP to 5.0)
        ))
        register(Material.NETHERITE_AXE, VanillaItemProfile(
            ItemRarity.EPIC, ItemType.AXE,
            mapOf(Stat.DAMAGE to 40.0, Stat.FORAGING_FORTUNE to 30.0, Stat.SWEEP to 10.0)
        ))

        // Shovels
        register(Material.WOODEN_SHOVEL, VanillaItemProfile(
            ItemRarity.COMMON, ItemType.NONE,
            mapOf(Stat.DAMAGE to 8.0)
        ))
        register(Material.STONE_SHOVEL, VanillaItemProfile(
            ItemRarity.COMMON, ItemType.NONE,
            mapOf(Stat.DAMAGE to 12.0)
        ))
        register(Material.GOLDEN_SHOVEL, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.NONE,
            mapOf(Stat.DAMAGE to 8.0)
        ))
        register(Material.IRON_SHOVEL, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.NONE,
            mapOf(Stat.DAMAGE to 16.0)
        ))
        register(Material.DIAMOND_SHOVEL, VanillaItemProfile(
            ItemRarity.RARE, ItemType.NONE,
            mapOf(Stat.DAMAGE to 20.0)
        ))
        register(Material.NETHERITE_SHOVEL, VanillaItemProfile(
            ItemRarity.EPIC, ItemType.NONE,
            mapOf(Stat.DAMAGE to 25.0)
        ))

        // Hoes
        register(Material.WOODEN_HOE, VanillaItemProfile(
            ItemRarity.COMMON, ItemType.HOE,
            mapOf(Stat.FARMING_FORTUNE to 5.0)
        ))
        register(Material.STONE_HOE, VanillaItemProfile(
            ItemRarity.COMMON, ItemType.HOE,
            mapOf(Stat.FARMING_FORTUNE to 10.0)
        ))
        register(Material.GOLDEN_HOE, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.HOE,
            mapOf(Stat.FARMING_FORTUNE to 25.0)
        ))
        register(Material.IRON_HOE, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.HOE,
            mapOf(Stat.FARMING_FORTUNE to 15.0)
        ))
        register(Material.DIAMOND_HOE, VanillaItemProfile(
            ItemRarity.RARE, ItemType.HOE,
            mapOf(Stat.FARMING_FORTUNE to 30.0)
        ))
        register(Material.NETHERITE_HOE, VanillaItemProfile(
            ItemRarity.EPIC, ItemType.HOE,
            mapOf(Stat.FARMING_FORTUNE to 50.0)
        ))
    }

    // endregion

    // region Bows & Ranged

    private fun registerBows() {
        register(Material.BOW, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.BOW,
            mapOf(Stat.DAMAGE to 30.0, Stat.CRIT_CHANCE to 5.0)
        ))
        register(Material.CROSSBOW, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.BOW,
            mapOf(Stat.DAMAGE to 35.0, Stat.CRIT_DAMAGE to 10.0)
        ))
    }

    // endregion

    // region Misc Gear

    private fun registerMiscGear() {
        register(Material.SHIELD, VanillaItemProfile(
            ItemRarity.UNCOMMON, ItemType.NONE,
            mapOf(Stat.DEFENSE to 25.0)
        ))
        register(Material.FISHING_ROD, VanillaItemProfile(
            ItemRarity.COMMON, ItemType.FISHING_ROD,
            mapOf(Stat.FISHING_SPEED to 10.0, Stat.SEA_CREATURE_CHANCE to 2.0)
        ))
        register(Material.TOTEM_OF_UNDYING, VanillaItemProfile(
            ItemRarity.LEGENDARY, ItemType.NONE
        ))
    }

    // endregion

    // region Blocks

    private fun registerBlocks() {
        // Ores — UNCOMMON
        for (mat in listOf(
            Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE,
            Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE,
            Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
            Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, Material.NETHER_GOLD_ORE,
            Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE,
            Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
            Material.NETHER_QUARTZ_ORE
        )) {
            register(mat, VanillaItemProfile(ItemRarity.UNCOMMON))
        }

        // Rare ores
        for (mat in listOf(
            Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
            Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE
        )) {
            register(mat, VanillaItemProfile(ItemRarity.RARE))
        }

        register(Material.ANCIENT_DEBRIS, VanillaItemProfile(ItemRarity.EPIC))

        // Spawner & command block
        register(Material.SPAWNER, VanillaItemProfile(ItemRarity.EPIC))
        register(Material.COMMAND_BLOCK, VanillaItemProfile(ItemRarity.SPECIAL))

        // Functional blocks
        register(Material.ENCHANTING_TABLE, VanillaItemProfile(ItemRarity.RARE))
        register(Material.ANVIL, VanillaItemProfile(ItemRarity.UNCOMMON))
        register(Material.BEACON, VanillaItemProfile(ItemRarity.LEGENDARY))
        register(Material.CONDUIT, VanillaItemProfile(ItemRarity.LEGENDARY))
        register(Material.END_PORTAL_FRAME, VanillaItemProfile(ItemRarity.EPIC))
        register(Material.DRAGON_EGG, VanillaItemProfile(ItemRarity.LEGENDARY))
    }

    // endregion

    // region Materials & Items

    private fun registerMaterials() {
        // Common materials
        for (mat in listOf(
            Material.STICK, Material.FLINT, Material.LEATHER, Material.STRING,
            Material.FEATHER, Material.BONE, Material.GUNPOWDER, Material.PAPER,
            Material.CLAY_BALL, Material.SUGAR_CANE, Material.WHEAT, Material.WHEAT_SEEDS,
            Material.COAL, Material.CHARCOAL, Material.RAW_COPPER, Material.RAW_IRON
        )) {
            register(mat, VanillaItemProfile(ItemRarity.COMMON))
        }

        // Uncommon materials
        for (mat in listOf(
            Material.IRON_INGOT, Material.COPPER_INGOT, Material.GOLD_INGOT,
            Material.REDSTONE, Material.LAPIS_LAZULI, Material.QUARTZ,
            Material.SLIME_BALL, Material.ENDER_PEARL, Material.BLAZE_ROD,
            Material.GHAST_TEAR, Material.MAGMA_CREAM, Material.PRISMARINE_SHARD,
            Material.PRISMARINE_CRYSTALS, Material.PHANTOM_MEMBRANE, Material.RAW_GOLD
        )) {
            register(mat, VanillaItemProfile(ItemRarity.UNCOMMON))
        }

        // Rare materials
        for (mat in listOf(
            Material.DIAMOND, Material.EMERALD, Material.AMETHYST_SHARD,
            Material.GOLDEN_APPLE, Material.EXPERIENCE_BOTTLE
        )) {
            register(mat, VanillaItemProfile(ItemRarity.RARE))
        }

        // Epic materials
        for (mat in listOf(
            Material.NETHERITE_SCRAP, Material.NETHERITE_INGOT,
            Material.ENCHANTED_GOLDEN_APPLE, Material.NETHER_STAR
        )) {
            register(mat, VanillaItemProfile(ItemRarity.EPIC))
        }

        // Legendary
        register(Material.END_CRYSTAL, VanillaItemProfile(ItemRarity.LEGENDARY))
        register(Material.HEART_OF_THE_SEA, VanillaItemProfile(ItemRarity.LEGENDARY))
    }

    // endregion
}
