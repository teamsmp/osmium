package uk.teamsmp.osmium.utils

import com.google.common.io.ByteStreams
import org.bukkit.entity.Player
import uk.teamsmp.osmium.Osmium
import uk.teamsmp.osmium.database.Database
import uk.teamsmp.osmium.database.executeQuery

fun Osmium.path(player: Player, server: String, messages: Boolean = true): Boolean {
    if (this.osmserver == server) {
        if (messages) player.sendMessage(mm.deserialize("${this.prefix} <red>You are already connected to <gold>${this.osmserver}<red>!"))
        return true
    }

    val out = ByteStreams.newDataOutput()

    // Using the executeQuery with parameterized query
    val query = "SELECT mark FROM servers WHERE name = ? LIMIT 1"
    val data = Database.getConnection().executeQuery(query, server)

    if (data.next()) {
        val mark = data.getString("mark")

        out.writeUTF("Connect")
        out.writeUTF(server)

        return when (mark) {
            "up" -> {
                player.sendPluginMessage(this, "BungeeCord", out.toByteArray())
                true
            }
            // If the server is down, offline, or flagged, and it's not 'hub-a', show the appropriate message
            "down" -> {
                if (player.hasPermission("osm.bypassDown")) {
                    player.sendMessage(mm.deserialize("${this.prefix} You have bypassed the <yellow>down</yellow> mark on <gold>$server</gold>."))
                    player.sendPluginMessage(this, "BungeeCord", out.toByteArray())
                    true
                } else if (server == "hub-a") {
                    this.path(player, "hub-b", messages)
                } else {
                    handleServerDown(player, server, messages, mark)
                }
            }

            "autodown", "flag" -> {
                if (server == "hub-a") {
                    // Fallback to hub-b if hub-a is unavailable
                    this.path(player, "hub-b", messages)
                } else {
                    handleServerDown(player, server, messages, mark)
                }
            }
            // Default: Return false if no conditions match
            else -> false
        }
    }
    return false
}

fun Osmium.handleServerDown(player: Player, server: String, messages: Boolean, mark: String): Boolean {
    val message = when (mark) {
        "down" -> "${this.prefix} <red>At the moment, <gold>$server<red> is not available. This is probably due to planned maintenance."
        "autodown" -> "${this.prefix} <red>At the moment, <gold>$server<red> is offline. Please try again in a minute"
        "flag" -> "${this.prefix} <gold>$server<red> is currently offline for an unknown reason! This is likely because of a crash - contact an Admin!"
        else -> null
    }

    if (messages && message != null) {
        player.sendMessage(this.mm.deserialize(message))
    }
    return false
}
