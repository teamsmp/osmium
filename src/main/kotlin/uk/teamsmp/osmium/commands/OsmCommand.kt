package uk.teamsmp.osmium.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import uk.teamsmp.osmium.Osmium
import uk.teamsmp.osmium.database.Database
import uk.teamsmp.osmium.database.executeQuery
import uk.teamsmp.osmium.database.executeUpdate

class OsmCommand(val plugin: Osmium) : CommandExecutor, TabCompleter {
    val mm = plugin.mm

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): Boolean {
        if (args.isNullOrEmpty()) {
            sender.sendMessage(mm.deserialize("${plugin.prefix} <red>Invalid usage!</red> Look at <gold>/osm help</gold> for more info."))
            return true
        }

        when (args[0]) {
            "status", "mark" -> {
                val status = Database.getConnection().executeQuery("SELECT mark FROM servers WHERE name = '${plugin.osmserver}' LIMIT 1")
                if (status.next()) {
                    val mark = status.getString("mark")
                    sender.sendMessage(mm.deserialize("${plugin.prefix} <gold>${plugin.osmserver}</gold> is currently marked as <yellow>${mark}</yellow>"))
                }
            }
            "all" -> {
                val status = Database.getConnection().executeQuery("SELECT name, mark FROM servers")
                var msg = ""
                msg += "${plugin.prefix} Status of all servers:"
                while (status.next()) {
                    msg += "<br>  <gold>${status.getString("name")} <gray>- <yellow>${status.getString("mark")}"
                }
                sender.sendMessage(mm.deserialize(msg))
            }
            "set" -> {
                if (args.size < 3) {
                    sender.sendMessage(mm.deserialize("${plugin.prefix} <red>Invalid usage!</red><br>  <yellow>/osm set <server> <mark>"))
                    return true
                }
                val server = args[1]
                val mark = args[2]

                Database.getConnection().executeUpdate("UPDATE servers SET mark = '${mark}' WHERE name = '${server}'")

                sender.sendMessage(mm.deserialize("${plugin.prefix} Set mark of <gold>${server}</gold> to <yellow>${mark}</yellow>."))
            }
            else -> {
                sender.sendMessage(mm.deserialize("${plugin.prefix} <red>Invalid usage!</red> <yellow>${args[0]}</yellow> is not a valid subcommand!"))
            }
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        cmd: Command,
        label: String,
        args: Array<out String>?
    ): List<String?>? {
        if (!args.isNullOrEmpty()) {
            if (args.size == 1) {
                return listOf("status", "mark", "all", "set")
            } else if (args.size == 2 && args[0] == "set") {
                val servers = Database.getConnection().executeQuery("SELECT name FROM servers")
                var res = listOf<String>()
                while (servers.next()) {
                    res.plus(servers.getString("name"))
                }
                return res
            } else if (args.size == 3 && args[0] == "set") {
                return listOf("up", "down", "flag", "autodown")
            }
        } else {
            return listOf()
        }
        return listOf()
    }
}