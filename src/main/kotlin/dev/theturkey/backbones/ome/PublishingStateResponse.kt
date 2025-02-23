package dev.theturkey.backbones.ome
import kotlinx.serialization.Serializable

@Serializable
data class PublishingStateResponse(
    var message: String,
    var statusCode: Int,
    var response: List<PublishingStateResponseData>? = null,
)
