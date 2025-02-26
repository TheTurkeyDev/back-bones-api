package dev.theturkey.backbones.rest.endpoints

import dev.theturkey.backbones.channel.Channel
import dev.theturkey.backbones.channel.ChannelManager
import dev.theturkey.backbones.channel.ChannelStreamKey
import dev.theturkey.backbones.channel.Restream
import dev.theturkey.backbones.errorMsg
import dev.theturkey.backbones.ome.OMEAPI
import dev.theturkey.backbones.rest.types.Tracks
import dev.theturkey.backbones.util.IdUtil
import dev.theturkey.backbones.util.TimeUtil
import jakarta.annotation.security.RolesAllowed
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@Path("channel")
class ChannelEndpoints {
    private val jsonSer = Json { encodeDefaults = true; ignoreUnknownKeys = true }
    private val invalidChannelId = "Invalid channel Id!"
    private val channelIdsDoNotMatch = "Channel id in URL does not match body channel id!"

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getChannels(): Response {
        val channels = ChannelManager.getChannels()
        return Response.ok().entity(jsonSer.encodeToString(channels)).build()
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    fun createChannels(json: String): Response {
        val channel = Json.decodeFromString<Channel>(json)
        channel.id = ChannelManager.createChannel(channel)
        channel.created = TimeUtil.nowStr()
        return Response.ok().entity(jsonSer.encodeToString(channel)).build()
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getChannel(@PathParam("id") id: String): Response {
        if (!ChannelManager.isValidId(id))
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(invalidChannelId)
        val channel = ChannelManager.getChannel(id.toLong()) ?: return Response.status(Response.Status.BAD_REQUEST)
            .errorMsg("Channel does not exist!")
        return Response.ok(jsonSer.encodeToString(channel)).build()
    }

    @Path("{id}")
    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    fun updateChannel(@PathParam("id") id: String, json: String): Response {
        if (!ChannelManager.isValidId(id))
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(invalidChannelId)

        val updated = jsonSer.decodeFromString<Channel>(json)
        if (updated.id != id.toLongOrNull())
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(channelIdsDoNotMatch)
        ChannelManager.updateChannel(updated)
        return Response.ok(updated).build()
    }

    @Path("{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    fun deleteChannel(@PathParam("id") id: String): Response {
        if (!ChannelManager.isValidId(id))
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(invalidChannelId)
        ChannelManager.deleteChannel(id.toLong())
        return Response.ok(JsonObject).build()
    }

    @GET
    @Path("{id}/streamkey")
    @Produces(MediaType.APPLICATION_JSON)
    fun getChannelStreamKey(@PathParam("id") id: String): Response {
        if (!ChannelManager.isValidId(id))
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(invalidChannelId)

        val channel = ChannelManager.getChannel(id.toLong()) ?: return Response.status(Response.Status.BAD_REQUEST)
            .errorMsg("Channel does not exist!")
        val streamKey = ChannelManager.getChannelStreamKey(channel.id)
        return Response.ok(jsonSer.encodeToString(streamKey)).build()
    }

    @PATCH
    @Path("{id}/streamkey")
    @Produces(MediaType.APPLICATION_JSON)
    fun reloadChannelStreamKey(@PathParam("id") id: String): Response {
        if (!ChannelManager.isValidId(id))
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(invalidChannelId)

        val channel = ChannelManager.getChannel(id.toLong()) ?: return Response.status(Response.Status.BAD_REQUEST)
            .errorMsg("Channel does not exist!")

        val key = IdUtil.randomUID(32)
        ChannelManager.setChannelStreamKey(channel.id, key)
        return Response.ok(jsonSer.encodeToString(ChannelStreamKey(key, channel.id))).build()
    }

    @Path("{id}/restreams")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getRestreams(@PathParam("id") id: String): Response {
        if (!ChannelManager.isValidId(id))
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(invalidChannelId)

        val jsonResp = jsonSer.encodeToString(ChannelManager.getRestreamsForChannel(id.toLong()))
        return Response.ok().entity(jsonResp).build()
    }

    @Path("{id}/restreams")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    fun addRestream(@PathParam("id") channelId: String, json: String): Response {
        if (!ChannelManager.isValidId(channelId))
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(invalidChannelId)

        val restreamData = jsonSer.decodeFromString<Restream>(json)
        if (java.lang.String.valueOf(restreamData.channelId) != channelId)
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(channelIdsDoNotMatch)

        restreamData.id = ChannelManager.addRestreamsForChannel(restreamData)

        if (restreamData.active && !OMEAPI.startPushPublish(restreamData))
            Response.serverError().errorMsg("Failed to start the restream in OME!")

        return Response.ok().entity(jsonSer.encodeToString(restreamData)).build()
    }

    @Path("{id}/restreams/{restreamId}")
    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    fun updateRestreams(
        @PathParam("id") channelId: String,
        @PathParam("restreamId") restreamId: String,
        json: String
    ): Response {
        if (!ChannelManager.isValidId(channelId))
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(invalidChannelId)

        //Not the same, but...
        if (!ChannelManager.isValidId(restreamId))
            return Response.status(Response.Status.BAD_REQUEST).errorMsg("Invalid restream Id")

        val restreamData = jsonSer.decodeFromString<Restream>(json)
        if (java.lang.String.valueOf(restreamData.channelId) != channelId)
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(channelIdsDoNotMatch)

        if (java.lang.String.valueOf(restreamData.id) != restreamId)
            return Response.status(Response.Status.BAD_REQUEST)
                .errorMsg("Restream id in URL does not match body restream id!")

        val oldRestreamData = ChannelManager.getRestream(restreamData.id)
        ChannelManager.updateRestream(restreamData)

        if ((oldRestreamData?.active == true) && !OMEAPI.stopPushPublish(oldRestreamData))
            Response.serverError().errorMsg("Failed to stop the old restream in OME!")

        // Probably don't need to sleep for 1 second, but without a sleep here these requests to OME are too fast and the old push isn't yet removed when the new push is attempted to be added
        Thread.sleep(1000)

        if (restreamData.active && !OMEAPI.startPushPublish(restreamData))
            Response.serverError().errorMsg("Failed to start the new restream in OME!")

        return Response.ok().entity(jsonSer.encodeToString(restreamData)).build()
    }

    @Path("{id}/restreams/{restreamId}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    fun deleteRestreams(@PathParam("id") channelId: String?, @PathParam("restreamId") restreamId: String): Response {
        if (!ChannelManager.isValidId(channelId))
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(invalidChannelId)

        //Not the same, but...
        if (!ChannelManager.isValidId(restreamId))
            return Response.status(Response.Status.BAD_REQUEST).errorMsg("Invalid restream Id")

        val restreamData = ChannelManager.getRestream(restreamId.toLong())
            ?: return Response.status(Response.Status.BAD_REQUEST).errorMsg("restream does not exist!")

        if (restreamData.active && !OMEAPI.stopPushPublish(restreamData))
            Response.serverError().errorMsg("Failed to stop the restream in OME!")

        ChannelManager.deleteRestream(restreamData.id)
        return Response.ok().entity(jsonSer.encodeToString(restreamData)).build()
    }
}


