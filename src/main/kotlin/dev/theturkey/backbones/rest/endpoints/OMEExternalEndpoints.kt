package dev.theturkey.backbones.rest.endpoints

import dev.theturkey.backbones.channel.ChannelManager
import dev.theturkey.backbones.errorMsg
import dev.theturkey.backbones.ome.OMEAPI
import dev.theturkey.backbones.rest.types.Tracks
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import kotlinx.serialization.json.Json

@Path("ome")
class OMEExternalEndpoints {

    @Path("tracks")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getAvailableTracks(): Response {
        //TODO don't hardcode profile?
        val outputs = OMEAPI.getOutputProfileInformation("bypass_stream")
        val videoTracks = outputs?.response?.encodes?.videos?.map { it.name }?.toList() ?: emptyList()
        val audioTracks = outputs?.response?.encodes?.audios?.map { it.name }?.toList() ?: emptyList()
        return Response.ok().entity(Json.encodeToString(Tracks(videoTracks, audioTracks))).build()
    }
}


