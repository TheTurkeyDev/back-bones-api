package dev.theturkey.backbones.ome

data class PublishingStateResponse(
    var message: String,
    var statusCode: Int,
    var response: List<PublishingStateResponseData>?,
)
