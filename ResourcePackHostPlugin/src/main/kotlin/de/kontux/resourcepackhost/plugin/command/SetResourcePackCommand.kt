package de.kontux.resourcepackhost.plugin.command

import de.kontux.resourcepackhost.plugin.ResourcePackHostPlugin
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetResourcePackCommand(private val plugin: ResourcePackHostPlugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage("You must be a player to execute this command.")
            return true
        }

        if (!sender.hasPermission("packhost.set")) {
            sender.sendMessage("${ChatColor.RED}You don't have the permission to set your resource pack!")
            return true
        }

        sender.setResourcePack(plugin.packUrl)
        return true
    }
}