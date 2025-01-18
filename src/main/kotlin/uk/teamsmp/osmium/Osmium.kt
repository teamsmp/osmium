package uk.teamsmp.osmium

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.plugin.java.JavaPlugin
import uk.teamsmp.osmium.commands.OsmCommand
import uk.teamsmp.osmium.database.Database
import uk.teamsmp.osmium.events.PlayerBroadcast
import java.util.logging.Level

class Osmium : JavaPlugin() {
    var osmserver = config.getString("server")
    var players: Int = 0
    val mm = MiniMessage.miniMessage()
    val prefix = "<dark_gray>[<gradient:aqua:dark_purple><b>OSM</b><dark_gray>]<reset>"

    override fun onEnable() {
        logger.log(Level.INFO, "OSMIUM IS ENABLING")

        saveDefaultConfig()
        logger.log(Level.INFO, "SAVED AND READ CONFIG FROM [config.yml]")
        osmserver = config.getString("server")

        Database.init(config, this@Osmium)

        server.pluginManager.registerEvents(PlayerBroadcast(this@Osmium), this)
        logger.log(Level.INFO, "EVENT [PLAYERBROADCAST] REGISTERED")

        getCommand("osm")?.apply {
            val cl = OsmCommand(this@Osmium)
            setExecutor(cl)
            tabCompleter = cl
        }
        logger.log(Level.INFO, "COMMAND [OSM] REGISTERED")

        logger.log(Level.INFO, "Ready!")
    }

    override fun onDisable() {
        Database.close()
    }
}
