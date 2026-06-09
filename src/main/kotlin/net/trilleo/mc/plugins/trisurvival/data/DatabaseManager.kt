package net.trilleo.mc.plugins.trisurvival.data

import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.util.logging.Logger

object DatabaseManager {

    private lateinit var connection: Connection
    private lateinit var logger: Logger

    fun init(plugin: JavaPlugin) {
        logger = plugin.logger
        val dbFile = File(plugin.dataFolder, "trisurvival.db")
        connection = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")
        connection.autoCommit = true
        createTables()
        logger.info("DatabaseManager initialised (${dbFile.name})")
    }

    fun getConnection(): Connection = connection

    fun shutdown() {
        if (::connection.isInitialized && !connection.isClosed) {
            connection.close()
            logger.info("DatabaseManager shut down")
        }
    }

    private fun createTables() {
        connection.createStatement().use { stmt ->
            stmt.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS player_skills (
                    uuid  TEXT    NOT NULL,
                    skill TEXT    NOT NULL,
                    xp    REAL    NOT NULL DEFAULT 0.0,
                    level INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY (uuid, skill)
                )
                """.trimIndent()
            )
        }
    }
}
