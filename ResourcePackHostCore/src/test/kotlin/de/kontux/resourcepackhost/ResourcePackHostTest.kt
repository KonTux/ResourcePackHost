package de.kontux.resourcepackhost

import org.junit.Test
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.net.URLConnection
import java.nio.channels.Channels


class ResourcePackHostTest {

    private companion object {
        const val PORT = 12345
        const val BACK_LOG = 5
        const val URL_PATH = "/test"

        //Link to pack: https://github.com/Unity-Resource-Pack/Unity
        val PACK_SOURCE = File("Unity-1.16.zip")
    }

    @Test
    fun test() {
        val packHost =  ResourcePackHost(PACK_SOURCE, PORT, BACK_LOG)
        packHost.startServer(URL_PATH)
        Thread.sleep(3000L) //Don't do that, just for testing
        downloadPack()
        packHost.stopServer()
    }

    private fun connect(): URLConnection {
        val url = URL("http://localhost:$PORT$URL_PATH")
        val connection = url.openConnection()
        connection.addRequestProperty("X-Minecraft-Username", "KonTux")
        connection.addRequestProperty("X-Minecraft-UUID", "10bcf559-56a0-4714-9c26-1e4299dc771d")
        connection.addRequestProperty("X-Minecraft-Version", "1.8.9")

        return connection
    }

    private fun downloadPack() {
        val destination = File("downloaded-pack.zip")
        downloadFile(destination, connect())
        require(
            PACK_SOURCE.readBytes().contentEquals(destination.readBytes())
        ) { "Downloaded file does not match original!" }
    }

    private fun downloadFile(destination: File, url: URLConnection) {
        if (!destination.exists()) {
            destination.createNewFile()
        }

        Channels.newChannel(url.getInputStream()).use { byteChannel ->
            FileOutputStream(destination).use { fileStream ->
                fileStream.channel.transferFrom(byteChannel, 0, Long.MAX_VALUE)
            }
        }
    }
}