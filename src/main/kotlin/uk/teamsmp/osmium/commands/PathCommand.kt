package uk.teamsmp.osmium.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import uk.teamsmp.osmium.Osmium
import uk.teamsmp.osmium.database.Database
import uk.teamsmp.osmium.database.executeQuery
import uk.teamsmp.osmium.utils.path

class PathCommand(val plugin: Osmium) : CommandExecutor, TabCompleter {
    val mm = plugin.mm

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): Boolean {
        if (sender is Player) {
            if (args.isNullOrEmpty()) {
                sender.sendMessage(mm.deserialize("${plugin.prefix} <red>Invalid usage!</red> <gray>Try <yellow>/path <server><gray>."))
                return true
            }
            val query = Database.getConnection().executeQuery("SELECT name FROM servers")
            val servers = mutableListOf<String>()
            while (query.next()) {
                servers.add(query.getString("name"))
            }

            if (args[0].isNotEmpty()) {
                if (servers.contains(args[0])) {
                    plugin.path(sender, args[0])
                } else {
                    sender.sendMessage(mm.deserialize("${plugin.prefix} <red>Server <gold>${args[0]}<red> does not exist!"))
                }
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): List<String?>? {
        if (!args.isNullOrEmpty()) {
            if (args.size == 1) {
                val servers = Database.getConnection().executeQuery("SELECT name FROM servers")
                var res = mutableListOf<String>()
                while (servers.next()) {
                    res.add(servers.getString("name"))
                }
                return res.filter { it.startsWith(args[0]) }
            }
        }
        return listOf()
    }
}