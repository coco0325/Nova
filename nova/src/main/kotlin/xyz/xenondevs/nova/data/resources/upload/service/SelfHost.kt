package xyz.xenondevs.nova.data.resources.upload.service

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bukkit.configuration.ConfigurationSection
import xyz.xenondevs.nova.data.config.DEFAULT_CONFIG
import xyz.xenondevs.nova.data.config.configReloadable
import xyz.xenondevs.nova.data.resources.builder.ResourcePackBuilder
import xyz.xenondevs.nova.data.resources.upload.UploadService
import xyz.xenondevs.nova.util.StringUtils
import xyz.xenondevs.nova.util.concurrent.Latch
import xyz.xenondevs.nova.util.data.getBooleanOrNull
import xyz.xenondevs.nova.util.data.getIntOrNull
import xyz.xenondevs.nova.util.data.http.ConnectionUtils
import xyz.xenondevs.nova.util.startsWithAny
import java.io.File
import kotlin.concurrent.thread

private val SELF_HOST_DELAY by configReloadable { DEFAULT_CONFIG.getLong("debug.self_host_delay") }

@Suppress("HttpUrlsUsage", "ExtractKtorModule")
internal object SelfHost : UploadService {
    
    override val name = "SelfHost"
    internal val startedLatch = Latch()
    
    private lateinit var server: JettyApplicationEngine
    
    private lateinit var host: String
    private var port = 38519
    private var appendPort = true
    
    private val url: String
        get() = buildString {
            if (!host.startsWithAny("http://", "https://"))
                append("http://")
            append(host)
            if (appendPort)
                append(":$port")
        }
    
    override fun loadConfig(cfg: ConfigurationSection) {
        startedLatch.close()
        val configuredHost = cfg.getString("host")
        this.host = configuredHost ?: ConnectionUtils.SERVER_IP
        
        val port = cfg.getIntOrNull("port")
        if (port != null) this.port = port
        appendPort = cfg.getBooleanOrNull("append_port") ?: cfg.getBooleanOrNull("portNeeded") ?: (configuredHost == null)
        
        thread(name = "ResourcePack Server", isDaemon = true) {
            server = embeddedServer(Jetty, port = this.port) {
                routing {
                    get("*") {
                        val packFile = ResourcePackBuilder.RESOURCE_PACK_FILE
                        if (packFile.exists()) call.respondFile(packFile)
                        else call.respond(HttpStatusCode.NotFound)
                    }
                }
                environment.monitor.subscribe(ServerReady) {
                    thread(isDaemon = true) {
                        startedLatch.open()
                    }
                }
            }
            server.start(wait = true)
        }
    }
    
    override fun disable() {
        server.stop(1000, 1000)
    }
    
    override suspend fun upload(file: File): String {
        return url + "/" + StringUtils.randomString(5) // https://bugs.mojang.com/browse/MC-251126
    }
    
}