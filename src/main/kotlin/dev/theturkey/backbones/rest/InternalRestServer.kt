package dev.theturkey.backbones.rest

import dev.theturkey.backbones.rest.endpoints.OMEEndpoints
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import java.io.IOException


class InternalRestServer(url: java.net.URI) {
    private var server: HttpServer? = null

    init {
        try {
            println("Internal Rest Server is initializing...")
            val config = ResourceConfig()
            config.register(UncaughtException::class.java)
            config.register(OMEEndpoints::class.java)

            config.setApplicationName("backbones_internal")
            config.property("jersey.config.server.wadl.disableWadl", "true")

            server = GrizzlyHttpServerFactory.createHttpServer(url, config, false)
            println("Internal Rest Server has been fully initialized Listening on $url")
        } catch (e: java.lang.Exception) {
            System.err.println("Internal Rest Server failed to be initialized")
            e.printStackTrace()
        }
    }

    fun start() {
        println("Internal Rest Server is starting")
        try {
            server?.start()
        } catch (e: IOException) {
            System.err.println("Internal Rest Server failed to be started")
            e.printStackTrace()
        }
        println("Internal Rest Server has been fully started")
    }
}