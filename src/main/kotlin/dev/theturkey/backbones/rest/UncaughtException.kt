package dev.theturkey.backbones.rest

import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotAuthorizedException
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Provider
class UncaughtException : Throwable(), ExceptionMapper<Throwable> {
    override fun toResponse(exception: Throwable): Response {
        if (exception is NotAuthorizedException)
            return exception.response

        if (exception is ForbiddenException) {
            val jsonObj = buildJsonObject { put("message", "You do not have permission to access that!") }
            return Response.status(Response.Status.UNAUTHORIZED).entity(Json.encodeToString(jsonObj)).build()
        }

        if (exception is NotFoundException) {
            val jsonObj = buildJsonObject { put("message", "Sorry that does not exist!") }
            return Response.status(Response.Status.NOT_FOUND).entity(Json.encodeToString(jsonObj)).build()
        }

        val jsonObj = buildJsonObject {
            put("message", "Something bad happened. Please let TurkeyDev know!")
            put("error", exception.toString())
        }

        return Response.status(500).entity(Json.encodeToString(jsonObj)).type(MediaType.APPLICATION_JSON).build()
    }
}