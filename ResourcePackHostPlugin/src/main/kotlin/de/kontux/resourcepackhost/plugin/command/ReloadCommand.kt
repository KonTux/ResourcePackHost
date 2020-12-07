package de.kontux.resourcepackhost.plugin.command

import de.kontux.resourcepackhost.plugin.ResourcePackHostPlugin
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ReloadCommand(private val plugin: ResourcePackHostPlugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!sender.hasPermission("packhost.reload")) {
            sender.sendMessage("${ChatColor.RED}You don't have the permission to reload this plugin!")
            return true
        }

        if (args.isEmpty() || args[0].let{ it != "reload" && it != "reload"}) {
            sender.sendMessage("${ChatColor.RED}/resourcepackhost reload")
            return true
        }

        plugin.reload()

        return true
    }

}