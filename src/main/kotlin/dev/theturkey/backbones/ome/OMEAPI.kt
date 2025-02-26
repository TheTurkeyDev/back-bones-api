package dev.theturkey.backbones.ome

import dev.theturkey.backbones.Config
import dev.theturkey.backbones.channel.Restream
import dev.theturkey.backbones.util.WebUtil
import dev.theturkey.backbones.util.WebUtil.getDefaultReq
import dev.theturkey.backbones.util.WebUtil.makeHTTPCall
import dev.theturkey.backbones.util.WebUtil.makeHTTPCallGetStatusCode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.net.http.HttpRequest

object OMEAPI {

    fun getOMEResponse(allowed: Boolean, reason: String?): String {
        return Json.encodeToString(buildJsonObject {
            put("allowed", allowed)
            put("reason", reason)
        })
    }

    private fun buildReq(path: String, method: String, body: String? = null): HttpRequest {
        val b = if (body == null) HttpRequest.BodyPublishers.noBody() else HttpRequest.BodyPublishers.ofString(body)
        return getDefaultReq(Config.OME_IP + path)
            .header("Authorization", "Basic dGVzdDp0b2tlbg==")
            .method(method, b)
            .build()
    }

    fun getLiveChannels(): List<String> {
        val req = buildReq("/v1/vhosts/default/apps/live/streams", "GET")
        val responseObject = WebUtil.getResponseAsJson<StreamsResponse>(req)
        return responseObject?.response ?: emptyList()
    }

    fun getStreamInfo(channelId: String): LiveStreamInfo? {
        val req = buildReq("/v1/vhosts/default/apps/live/streams/$channelId", "GET")
        val responseObject = WebUtil.getResponseAsJson<LiveStreamInfoResponse>(req)
        return responseObject?.response
    }


    fun getCurrentPushPublish(restreamData: Restream): PublishingStateResponse? {
        val body = "{\"id\": \"push_" + restreamData.channelId + "_" + restreamData.id + "\"}"
        val request = buildReq("/v1/vhosts/default/apps/live:pushes", "POST", body)
        val response = makeHTTPCall(request)
        val statusCode = response?.statusCode() ?: -1
        if (statusCode != 200) {
            println("Received a response code that wasn't a 200! $statusCode")
            return null
        }
        return WebUtil.getResponseAsJson<PublishingStateResponse>(response)
    }

    fun startPushPublish(restreamData: Restream): Boolean {
        val baseUrl = restreamData.url
        // TODO
        // return fmt.Errorf("%s is not a currently supported platform", restreamData.Platform)
        val responseObject = getCurrentPushPublish(restreamData)
        if (responseObject?.response?.isEmpty() == true) {
            val postData = StartPushRequest(
                "push_" + restreamData.channelId + "_" + restreamData.id,
                RequestStreamData(
                    restreamData.channelId.toString(),
                    listOf(restreamData.videoTrack, restreamData.audioTrack)
                ),
                "rtmp",
                baseUrl,
                restreamData.streamKey
            )

            val request = buildReq("/v1/vhosts/default/apps/live:startPush", "POST", Json.encodeToString(postData))
            val statusCode = makeHTTPCallGetStatusCode(request)
            if (statusCode != 200)
                println("received a response code that wasn't a 200! $statusCode")
            return statusCode == 200
        }
        return false
    }

    fun stopPushPublish(restreamData: Restream): Boolean {
        val responseObject = getCurrentPushPublish(restreamData)

        if (responseObject?.response?.isNotEmpty() == true) {
            val postData =
                StartPushRequest("push_" + restreamData.channelId + "_" + restreamData.id, null, null, null, null)
            val request = buildReq("/v1/vhosts/default/apps/live:stopPush", "POST", Json.encodeToString(postData))

            val statusCode = makeHTTPCallGetStatusCode(request)
            if (statusCode != 200)
                println("received a response code that wasn't a 200! $statusCode")
            return statusCode == 200
        }
        return false
    }

    fun getOutputProfileInformation(profile: String): OutputProfileInformationResponse? {
        val request = buildReq("/v1/vhosts/default/apps/live/outputProfiles/$profile", "GET")
        val response = makeHTTPCall(request)
        val statusCode = response?.statusCode() ?: -1
        if (statusCode != 200) {
            println("Received a response code that wasn't a 200! $statusCode")
            return null
        }
        return WebUtil.getResponseAsJson<OutputProfileInformationResponse>(response)
    }
}
