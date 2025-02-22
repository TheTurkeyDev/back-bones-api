package dev.theturkey.backbones

import dev.theturkey.backbones.rest.InternalRestServer
import dev.theturkey.backbones.rest.RestServer
import dev.theturkey.backbones.rest.endpoints.ErrorResponse
import jakarta.ws.rs.core.Response
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.URISyntaxException

fun main() {
    start()
}

fun start() {
    try {
        RestServer(URI("http://0.0.0.0:8081")).start()
        InternalRestServer(URI("http://0.0.0.0:8082")).start()
    } catch (e: URISyntaxException) {
        e.printStackTrace()
    }
}

fun Response.ResponseBuilder.errorMsg(msg: String): Response {
    return entity(Json.encodeToString(ErrorResponse(msg))).build()
}