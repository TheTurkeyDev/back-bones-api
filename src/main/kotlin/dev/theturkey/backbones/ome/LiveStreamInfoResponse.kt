package dev.theturkey.backbones.ome
import kotlinx.serialization.Serializable

@Serializable
data class LiveStreamInfoResponse (
    var message: String,
    var statusCode: Int,
    var response: LiveStreamInfo? = null
)
