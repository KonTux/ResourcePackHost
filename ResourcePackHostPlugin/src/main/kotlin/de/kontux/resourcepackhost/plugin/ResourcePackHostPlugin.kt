package de.kontux.resourcepackhost.plugin

import de.kontux.resourcepackhost.ResourcePackHost
import de.kontux.resourcepackhost.plugin.command.ReloadCommand
import de.kontux.resourcepackhost.plugin.command.SetResourcePackCommand
import de.kontux.resourcepackhost.plugin.listener.JoinListener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class ResourcePackHostPlugin : JavaPlugin() {

    private lateinit var packHost: ResourcePackHost
    private lateinit var configuration: Config
    val packUrl: String
        get() = configuration.finalUrl
    val onJoin: Boolean
        get() = configuration.onJoin

    override fun onEnable() {
        logger.info("Starting ResourcePackHost...")

        saveDefaultConfig()
        loadConfiguration()

        try {
            packHost = ResourcePackHost(configuration.packFile, configuration.port, configuration.backlog)
            packHost.startServer(configuration.urlPath, configuration.threads)
            logger.info("ResourcePackHost has been started successfully.")
        } catch (e: Exception) {
            server.scheduler.scheduleSyncDelayedTask(this, {
                logger.warning("==========================")
                logger.warning("Could not start ResourcePackHost: ${e.message}")
                logger.warning("Please check your config.yml!")
                logger.warning("Plugin will be disabled...")
                logger.warning("==========================")
                server.pluginManager.disablePlugin(this)
            }, 80L)
        }

        getCommand("resourcepack").executor = SetResourcePackCommand(this)
        getCommand("resourcepackhost").executor = ReloadCommand(this)
        server.pluginManager.registerEvents(JoinListener(this), this)
    }

    override fun onDisable() {
        if (this::packHost.isInitialized) {
            packHost.stopServer()
        }
    }

    private fun loadConfiguration() {
        config.let {
            val path = it.getString("pack", "")
            val port = it.getInt("port", 12345)
            val urlPath = it.getString("url-path", "/resourcepack")
            val backlog = it.getInt("backlog", 100)
            val threads = it.getInt("threads", 2)
            val onJoin = it.getBoolean("on-join", false)

            configuration = Config(path, urlPath, port, backlog, threads, onJoin)
        }
    }

    internal fun reload() {
        packHost.stopServer()
        reloadConfig()
        loadConfiguration()
        packHost = ResourcePackHost(configuration.packFile, configuration.port, configuration.backlog)
        packHost.startServer(configuration.urlPath)
    }

    private fun getServerAddress(): String {
        return server.ip.also {
            require(it.isNotEmpty()) { "Please set your server's address in the server.properties file in order to use ResourcePackHost!" }
        }
    }

    inner class Config(path: String, val urlPath: String, val port: Int, val backlog: Int, val threads: Int, val onJoin: Boolean) {
        val packFile: File = File(dataFolder, path)
        val finalUrl = "http://${getServerAddress()}:$port$urlPath"
    }
}