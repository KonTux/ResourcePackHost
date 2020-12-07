package de.kontux.resourcepackhost.plugin.listener

import de.kontux.resourcepackhost.plugin.ResourcePackHostPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinListener(private val plugin: ResourcePackHostPlugin) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (plugin.onJoin) {
            event.player.setResourcePack(plugin.packUrl)
        }
    }

}