package net.trilleo.mc.plugins.trisurvival

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.trilleo.mc.plugins.trisurvival.collections.CollectionManager
import net.trilleo.mc.plugins.trisurvival.collections.CollectionRegistry
import net.trilleo.mc.plugins.trisurvival.config.PluginConfig
import net.trilleo.mc.plugins.trisurvival.crafting.CraftingRecipeRegistry
import net.trilleo.mc.plugins.trisurvival.data.DatabaseManager
import net.trilleo.mc.plugins.trisurvival.data.PlayerDataManager
import net.trilleo.mc.plugins.trisurvival.data.ServerDataManager
import net.trilleo.mc.plugins.trisurvival.enchants.EnchantRegistry
import net.trilleo.mc.plugins.trisurvival.reforges.ReforgeRegistry
import net.trilleo.mc.plugins.trisurvival.ores.CustomOreRegistry
import net.trilleo.mc.plugins.trisurvival.registration.*
import net.trilleo.mc.plugins.trisurvival.skills.SkillManager
import net.trilleo.mc.plugins.trisurvival.stats.StatManager
import net.trilleo.mc.plugins.trisurvival.utils.MessageUtil
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    /** Typed configuration wrapper – available after [onEnable]. */
    lateinit var pluginConfig: PluginConfig
        private set

    override fun onEnable() {
        // Load configuration
        logger.info("Loading configuration...")
        pluginConfig = PluginConfig(this)
        MessageUtil.init(pluginConfig.messagePrefix)

        // Initialise data managers
        logger.info("Initialising data managers...")
        ServerDataManager.init(this)
        PlayerDataManager.init(this)

        // Initialise database and game engines
        logger.info("Initialising database...")
        DatabaseManager.init(this)
        logger.info("Initialising skill engine...")
        SkillManager.init(this)
        logger.info("Initialising stat engine...")
        StatManager.init(this)
        logger.info("Registering enchants...")
        EnchantRegistry.init(this)
        logger.info("Registering reforges...")
        ReforgeRegistry.init(this)

        // Register custom items and recipes
        logger.info("Registering custom items...")
        ItemRegistrar.registerAll(this)
        logger.info("Registering recipes...")
        RecipeRegistrar.registerAll(this)
        logger.info("Initialising custom crafting registry...")
        CraftingRecipeRegistry.init(this)
        logger.info("Registering custom ores...")
        CustomOreRegistry.init(this)
        logger.info("Registering collections...")
        CollectionRegistry.init(this)
        CollectionManager.init(this)

        // Register commands, listeners, GUIs and tasks
        logger.info("Registering commands...")
        CommandRegistrar.registerAll(this)
        logger.info("Registering permissions...")
        PermissionRegistrar.registerAll(this)
        logger.info("Registering listeners...")
        ListenerRegistrar.registerAll(this)
        logger.info("Registering GUIs...")
        GUIManager.registerAll(this)
        logger.info("Registering tasks...")
        TaskRegistrar.registerAll(this)

        logger.info("Plugin enabled!")
    }

    override fun onDisable() {
        logger.info("Disabling plugin...")

        // Disconnect all players
        for (player in server.onlinePlayers) {
            player.kick(Component.text("Server closed. Coming back soon...").color(NamedTextColor.RED))
        }

        // Cancel all scheduled tasks
        TaskRegistrar.unregisterAll()

        // Remove all registered recipes
        RecipeRegistrar.unregisterAll()

        // Save skill data and shut down database
        SkillManager.saveAll()
        StatManager.cleanup()
        DatabaseManager.shutdown()

        // Persist data for any players still online and server-wide data
        PlayerDataManager.saveAll()
        ServerDataManager.save()

        logger.info("Plugin disabled!")
    }
}
