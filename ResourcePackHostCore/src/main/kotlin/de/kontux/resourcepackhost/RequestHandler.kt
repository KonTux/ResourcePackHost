package de.kontux.resourcepackhost

import com.sun.istack.internal.logging.Logger
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler

class RequestHandler(private val packHost: ResourcePackHost) : HttpHandler {

    companion object {
        private const val REQUEST_RATE = 500L
        private val INVALID_REQUEST =
            "Your HTTP request does not seem to be sent from a Minecraft Client!".toByteArray()
    }

    private val lastRequests = HashMap<String, Long>()
    private val logger = Logger.getLogger(RequestHandler::class.java)

    override fun handle(exchange: HttpExchange) {
        val remoteAddress = exchange.remoteAddress.address.hostAddress
        if (lastRequests[remoteAddress].let { it != null && it + REQUEST_RATE > System.currentTimeMillis() }) {
            exchange.sendResponseHeaders(403, 0)
            exchange.close()
            return
        } else {
            lastRequests[remoteAddress] = System.currentTimeMillis()
        }

        if (!isValidRequest(exchange)) {
            exchange.sendResponseHeaders(400, INVALID_REQUEST.size.toLong())
            exchange.responseBody.use {
                it.write(INVALID_REQUEST)
                it.flush()
            }

            exchange.close()
            return
        }

        try {
            setPackHeaders(exchange)
            sendPackBody(exchange)
            exchange.close()
        } catch (e: Exception) {
            /*
            Seems to happen if client notices that it already has this pack
            It works anyway, so we can ignore it
            */
            if (e.message != "Broken pipe") {
                logger.warning("Could not handle HTTP request", e)
            }
        }
    }

    private fun setPackHeaders(exchange: HttpExchange) {
        exchange.responseHeaders.let { headers ->
            val pack = packHost.resourcePack

            headers["Connection"] = "Keep-Alive"
            headers["Content-Disposition"] = "attachment; filename=\"${pack.name}\""
            headers["Content-Type"] = "application/zip"
            headers["Keep-Alive"] = "timeout=5, max=100"
        }
    }

    private fun sendPackBody(exchange: HttpExchange) {
        val pack = packHost.packBytes
        exchange.sendResponseHeaders(200, pack.size.toLong())

        exchange.responseBody.use {
            it.write(pack)
            it.flush()
        }
    }

    private fun isValidRequest(exchange: HttpExchange): Boolean {
        return exchange.requestHeaders.let {
            it.containsKey("X-Minecraft-Username") &&
                    it.containsKey("X-Minecraft-UUID") && it.containsKey("X-Minecraft-Version")
        }
    }
}