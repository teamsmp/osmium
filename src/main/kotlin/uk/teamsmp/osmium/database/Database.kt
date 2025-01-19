package uk.teamsmp.osmium.database

import org.bukkit.configuration.file.FileConfiguration
import uk.teamsmp.osmium.Osmium
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.logging.Level

object Database {
    private lateinit var connection: Connection

    fun init(config: FileConfiguration, plugin: Osmium) {
        val host = config.getString("database.host") ?: "localhost"
        val port = config.getInt("database.port", 3306)
        val database = config.getString("database.database") ?: "minecraft"
        val username = config.getString("database.username") ?: "root"
        val password = config.getString("database.password") ?: ""
        val ssl = if (config.getBoolean("database.ssl")) "?useSSL=true" else "?useSSL=false"

        val url = "jdbc:mysql://$host:$port/$database$ssl"
        try {
            connection = DriverManager.getConnection(url, username, password)
            plugin.logger.info("MySQL connection established!")
        } catch (e: SQLException) {
            e.printStackTrace()
            plugin.logger.log(Level.SEVERE, "Could not establish MySQL connection!")
        }
    }

    fun getConnection(): Connection = connection

    fun close() {
        try {
            if (::connection.isInitialized && !connection.isClosed) {
                connection.close()
                println("MySQL connection closed.")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}
