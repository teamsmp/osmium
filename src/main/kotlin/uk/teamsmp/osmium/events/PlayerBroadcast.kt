package uk.teamsmp.osmium.events

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import uk.teamsmp.osmium.Osmium
import uk.teamsmp.osmium.database.Database
import uk.teamsmp.osmium.database.executeUpdate

class PlayerBroadcast(val plugin: Osmium) : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        this.plugin.players = Bukkit.getOnlinePlayers().size
        Database.getConnection().executeUpdate("UPDATE servers SET players = ${this.plugin.players} WHERE name = '${this.plugin.osmserver}'")
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        this.plugin.players = Bukkit.getOnlinePlayers().size
        Database.getConnection().executeUpdate("UPDATE servers SET players = ${this.plugin.players} WHERE name = '${this.plugin.osmserver}'")
    }
}