package dev.theturkey.backbones.ome

data class StreamsResponse(
    var message: String,
    var statusCode: Int,
    var response: List<String>?
)
