package dev.theturkey.backbones.ome

data class StartRecordingRequest(
    var id: String,
    var stream: RequestStreamData,
    var interval: Int?,
    var segmentationRule: String?
)
