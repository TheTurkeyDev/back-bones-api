package dev.theturkey.backbones.ome

data class StartPushRequest(
    var id: String,
    var stream: RequestStreamData?,
    var protocol: String?,
    var url: String?,
    var streamKey: String?
)
