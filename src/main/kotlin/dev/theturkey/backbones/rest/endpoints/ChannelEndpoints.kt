package dev.theturkey.backbones.rest.endpoints

import dev.theturkey.backbones.DatabaseCore
import dev.theturkey.backbones.DatabaseCore.channels
import dev.theturkey.backbones.channel.Channel
import dev.theturkey.backbones.channel.ChannelManager
import dev.theturkey.backbones.channel.Restream
import dev.theturkey.backbones.errorMsg
import dev.theturkey.backbones.ome.OMEAPI
import jakarta.annotation.security.RolesAllowed
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import kotlinx.serialization.json.Json
import org.ktorm.entity.toList

@Path("channel")
class ChannelEndpoints {
    private val jsonSer = Json { encodeDefaults = true }
    private val invalidChannelId = "Invalid channel Id!"
    private val channelIdsDoNotMatch = "Channel id in URL does not match body channel id!"

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getChannels(): Response {
        val channels = DatabaseCore.DB.channels.toList()
        return Response.ok().entity(jsonSer.encodeToString(channels)).build()
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    fun createChannels(): Response {
        val channels = DatabaseCore.DB.channels.toList()
        return Response.ok().entity(jsonSer.encodeToString(channels)).build()
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getChannel(@PathParam("id") id: String): Response {
        if (!ChannelManager.isValidId(id))
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(invalidChannelId)
        val channel = ChannelManager.getChannel(id.toLong()) ?: return Response.status(Response.Status.BAD_REQUEST)
            .errorMsg("Channel does not exist!")
        return Response.ok(channel).build()
    }

    @Path("{id}")
    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    fun updateChannel(@PathParam("id") id: String, json: String): Response {
        if (!ChannelManager.isValidId(id))
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(invalidChannelId)

        val updated: Channel = jsonSer.decodeFromString<Channel>(json)
        if (updated.id != id.toLongOrNull())
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(channelIdsDoNotMatch)
        ChannelManager.updateChannel(updated)
        return Response.ok(updated).build()
    }

    @Path("{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    fun deleteChannel(@PathParam("id") id: String): Response {
        if (!ChannelManager.isValidId(id))
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(invalidChannelId)
        ChannelManager.deleteChannel(id.toLong())
        return Response.ok().build()
    }

    @GET
    @Path("{id}/streamkey")
    @Produces(MediaType.APPLICATION_JSON)
    fun getChannelStreamKey(@PathParam("id") id: String): Response {
        if (!ChannelManager.isValidId(id))
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(invalidChannelId)

        val channel = ChannelManager.getChannel(id.toLong()) ?: return Response.status(Response.Status.BAD_REQUEST)
            .errorMsg("Channel does not exist!")
        return Response.ok(ChannelManager.getChannelStreamKey(channel.id)).build()
    }

    @PATCH
    @Path("{id}/streamkey")
    @Produces(MediaType.APPLICATION_JSON)
    fun reloadChannelStreamKey(@PathParam("id") id: String): Response {
        if (!ChannelManager.isValidId(id))
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(invalidChannelId)

        val channel = ChannelManager.getChannel(id.toLong()) ?: return Response.status(Response.Status.BAD_REQUEST)
            .errorMsg("Channel does not exist!")

        val key = "NEW_KEY"
        ChannelManager.setChannelStreamKey(channel.id, key)
        return Response.ok().build()
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
    fun addRestreams(@PathParam("id") channelId: String, json: String): Response {
        if (!ChannelManager.isValidId(channelId))
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(invalidChannelId)

        val restreamData = jsonSer.decodeFromString<Restream>(json)
        if (java.lang.String.valueOf(restreamData.channelId) != channelId)
            return Response.status(Response.Status.BAD_REQUEST).errorMsg(channelIdsDoNotMatch)

        ChannelManager.addRestreamsForChannel(restreamData)

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


