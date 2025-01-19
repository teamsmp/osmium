package uk.teamsmp.osmium.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerCommandEvent
import org.bukkit.event.server.ServerLoadEvent
import uk.teamsmp.osmium.Osmium
import uk.teamsmp.osmium.database.Database
import uk.teamsmp.osmium.database.executeQuery
import uk.teamsmp.osmium.database.executeUpdate

class AutodownListener(val plugin: Osmium) : Listener {
    @EventHandler
    fun onServerLoad(event: ServerLoadEvent) {
        val query =
            Database.getConnection().executeQuery("SELECT mark FROM servers WHERE name = ? LIMIT 1", plugin.osmserver)
        if (query.next()) {
            val mark = query.getString("mark")
            if (mark == "autodown") {
                Database.getConnection()
                    .executeUpdate("UPDATE servers SET mark = 'up' WHERE name = ?", plugin.osmserver)
                plugin.logger.info("MARKED SERVER AS [UP]")
            }
        }
    }

    @EventHandler
    fun onServerStop(event: ServerCommandEvent) {
        if (event.command == "stop") {
            val query = Database.getConnection()
                .executeQuery("SELECT mark FROM servers WHERE name = ? LIMIT 1", plugin.osmserver)
            if (query.next()) {
                val mark = query.getString("mark")
                if (mark == "up") {
                    Database.getConnection().executeUpdate("UPDATE servers SET mark = 'autodown' WHERE name = '${plugin.osmserver}'")
                    plugin.logger.info("MARKED SERVER AS [AUTODOWN]")
                }
            }
        }
    }
}