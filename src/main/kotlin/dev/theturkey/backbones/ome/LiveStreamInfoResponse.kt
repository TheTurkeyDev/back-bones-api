package dev.theturkey.backbones.ome

data class LiveStreamInfoResponse (
    var message: String,
    var statusCode: Int,
    var response: LiveStreamInfo?
)
