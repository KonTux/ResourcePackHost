# ResourcePackHost

Host resource packs directly on a Minecraft server instead of using a third party hosting service!

This can especially be useful when resource packs are generated dynamically.

Keep in mind that this will bind an HTTP server to a (configurable) port, so
make sure the hosting service you are using/your PC allows that.

**This repository consists of two parts:**

* Core - The core software that handles HTTP requests, can be used & shaded in any plugin
* Plugin - A simple plugin that uses the core part and makes its functionality run on a Spigot Server


**Using the Plugin**:

* Make sure the "server-ip" is set to the server's IP/address
* Place the resource pack in the plugin's folder
* Set the "pack" value to the path/filename of the pack's zip-file
* Configure other things like port, backlog, amount of threads,
 automatic load etc. to your needs (Optional)
  
* **Start your server and enjoy!**

**Using it in your own plugin:**

Add the jitpack repository:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add this project as a dependency:

```xml
<dependency>
    <groupId>com.github.KonTux</groupId>
    <artifactId>ResourcePackHost</artifactId>
    <version>1.0</version>
</dependency>
```

Example usage:

```kotlin
class Example : JavaPlugin() {
    
    private lateinit var packHost: ResourcePackHost
    
    override fun onEnable() {
        packHost = ResourcePackHost(RESOURCE_PACK_FILE, PORT, BACK_LOG)
        packHost.startServer("/resourcepack", AMOUNT_OF_THREADS) //Will start the HTTP server at the given url path
    }
    
    override fun onDisable() {
        packHost.stopServer()
    }
    
}
```

The example above will start hosting the resource pack loaded from
the given file at `http://$serverUrl:$port/$urlPath`.
You can use this url to let players load the resource pack using
`player.setResourcePack(url);`.

**Make sure to shade the dependency into your jar!**
(http://maven.apache.org/plugins/maven-shade-plugin/)
