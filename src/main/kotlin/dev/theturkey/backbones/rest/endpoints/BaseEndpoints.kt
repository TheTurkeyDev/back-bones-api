package dev.theturkey.backbones.rest.endpoints

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/")
class BaseEndpoints {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun ping(): Response {
        return Response.ok().build()
    }
}
