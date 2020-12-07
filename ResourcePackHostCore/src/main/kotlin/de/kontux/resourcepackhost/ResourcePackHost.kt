package de.kontux.resourcepackhost

import com.sun.net.httpserver.HttpServer
import java.io.File
import java.net.InetSocketAddress
import java.util.concurrent.Executors

class ResourcePackHost(initialPack: File, port: Int = 12345, backlog: Int = 100) {

    init {
        require(initialPack.exists()) { "$initialPack does not exist!" }
        require(port > 1024) { "Port must be above 1024!" }
        require(backlog > 1) { "Backlog must be at least 2!" }
    }

    var resourcePack: File = initialPack
        set(value) {
            field = value
            try {
                packBytes = resourcePack.readBytes()
            } catch (e: Exception) {
                throw IllegalArgumentException("Could not read from given ResourcePack file!", e)
            }
        }

    internal var packBytes: ByteArray = initialPack.readBytes()

    private val server = HttpServer.create(InetSocketAddress(port), backlog)

    fun startServer(urlPath: String = "/", threads: Int = 2) {
        try {
            server.createContext(urlPath, RequestHandler(this))
            server.executor = Executors.newFixedThreadPool(threads)
            server.start()
            println("Listening for pack requests on port ${server.address.port} at path $urlPath")
        } catch (e: Exception) {
            throw RuntimeException("Could not start HTTP server", e)
        }
    }

    fun stopServer() {
        server.stop(0)
    }

}

