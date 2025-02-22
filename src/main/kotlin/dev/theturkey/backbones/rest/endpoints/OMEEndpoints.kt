package dev.theturkey.backbones.rest.endpoints

import dev.theturkey.backbones.channel.ChannelManager
import dev.theturkey.backbones.ome.AdmissionPayload
import dev.theturkey.backbones.ome.OMEAPI
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.net.URI
import java.net.URISyntaxException
import java.util.regex.Matcher
import java.util.regex.Pattern

@Path("ome")
class OMEEndpoints {

    private val streamKeyPattern: Pattern = Pattern.compile("^/[a-zA-Z0-9]+/?$")

    @POST
    @Path("on-publish")
    fun onPublish(json: String): Response {
        val data = Json.decodeFromString<AdmissionPayload>(json)
        println("ON Publish! " + data.request.url)
        val url: URI
        try {
            url = URI(data.request.url)
        } catch (e: URISyntaxException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(OMEAPI.getOMEResponse(false, e.message))
                .build()
        }

        if (data.request.direction == "outgoing")
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(OMEAPI.getOMEResponse(false, "Outgoing not supported!")).build()

        val matcher: Matcher = streamKeyPattern.matcher(url.path)
        if (!matcher.find())
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(OMEAPI.getOMEResponse(false, "Invalid stream key!")).build()

        val parts = url.path.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val key = parts[1]
        val channelId: Long = ChannelManager.getChannelFromStreamKey(key)

        if (channelId == -1L)
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(OMEAPI.getOMEResponse(false, "Invalid stream key!")).build()

        val jsonObject = buildJsonObject {
            put("allowed", true)
            put("reason", "authorized")
            put("new_url", url.resolve("/live/$channelId").toString())
        }

        if (data.request.status.equals("opening"))
            initStream(channelId)
        else
            endStream(channelId)

        return Response.ok().entity(jsonObject).build()
    }

    private fun initStream(channelId: Long) {
        println("Stream started! $channelId")
    }

    private fun endStream(channelId: Long) {
        println("Stream ended! $channelId")
    }
}
