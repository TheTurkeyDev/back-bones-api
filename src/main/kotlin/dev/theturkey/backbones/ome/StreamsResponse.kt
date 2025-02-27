package dev.theturkey.backbones.ome

import kotlinx.serialization.Serializable

@Serializable
data class StreamsResponse(
    var message: String,
    var statusCode: Int,
    var response: List<String>? = null
)
