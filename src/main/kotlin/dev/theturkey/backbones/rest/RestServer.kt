package dev.theturkey.backbones.rest

import dev.theturkey.backbones.rest.endpoints.BaseEndpoints
import dev.theturkey.backbones.rest.endpoints.ChannelEndpoints
import dev.theturkey.backbones.rest.endpoints.OMEEndpoints
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature
import java.io.IOException


class RestServer(url: java.net.URI) {
    private var server: HttpServer? = null

    init {
        try {
            println("Rest Server is initializing...")
            val config = ResourceConfig()
            config.register(RolesAllowedDynamicFeature::class.java)
            config.register(UncaughtException::class.java)
            config.register(BaseEndpoints::class.java)
            config.register(ChannelEndpoints::class.java)

            config.setApplicationName("backbones")
            config.property("jersey.config.server.wadl.disableWadl", "true")

            server = GrizzlyHttpServerFactory.createHttpServer(url, config, false)
            println("Rest Server has been fully initialized Listening on $url")
        } catch (e: java.lang.Exception) {
            System.err.println("Rest Server failed to be initialized")
            e.printStackTrace()
        }
    }

    fun start() {
        println("Rest Server is starting")
        try {
            server?.start()
        } catch (e: IOException) {
            System.err.println("Rest Server failed to be started")
            e.printStackTrace()
        }
        println("Rest Server has been fully started")
    }
}