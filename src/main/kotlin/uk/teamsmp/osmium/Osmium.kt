package uk.teamsmp.osmium

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.plugin.java.JavaPlugin
import uk.teamsmp.osmium.commands.HubCommand
import uk.teamsmp.osmium.commands.OsmCommand
import uk.teamsmp.osmium.commands.PathCommand
import uk.teamsmp.osmium.database.Database
import uk.teamsmp.osmium.events.AutodownListener
import uk.teamsmp.osmium.events.PlayerBroadcast

class Osmium : JavaPlugin() {
    var osmserver: String = config.getString("server").toString()
    var players: Int = 0
    val mm = MiniMessage.miniMessage()
    val prefix = "<dark_gray>[<gradient:aqua:dark_purple><b>OSM</b><dark_gray>]<reset>"

    override fun onEnable() {
        logger.info("OSMIUM IS ENABLING")

        server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")
        logger.info("REGISTERED [BUNGEE] PLUGIN CHANNEL")

        saveDefaultConfig()
        logger.info("SAVED AND READ CONFIG FROM [config.yml]")
        osmserver = config.getString("server").toString()

        Database.init(config, this@Osmium)

        server.pluginManager.registerEvents(PlayerBroadcast(this@Osmium), this)
        logger.info("EVENT [PLAYERBROADCAST] REGISTERED")

        server.pluginManager.registerEvents(AutodownListener(this@Osmium), this)
        logger.info("EVENT [AUTODOWN] REGISTERED")

        getCommand("osm")?.apply {
            val cmd = OsmCommand(this@Osmium)
            setExecutor(cmd)
            tabCompleter = cmd
        }
        logger.info("COMMAND [OSM] REGISTERED")
        
        getCommand("hub")?.apply { 
            val cmd = HubCommand(this@Osmium)
            setExecutor(cmd)
            tabCompleter = cmd
        }
        logger.info("COMMAND [HUB] REGISTERED")

        getCommand("path")?.apply {
            val cmd = PathCommand(this@Osmium)
            setExecutor(cmd)
            tabCompleter = cmd
        }
        logger.info("COMMAND [PATH] REGISTERED")

        logger.info("Ready!")
    }

    override fun onDisable() {
        Database.close()
    }
}
