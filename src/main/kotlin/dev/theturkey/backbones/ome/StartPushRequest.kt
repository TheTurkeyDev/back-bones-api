package dev.theturkey.backbones.ome
import kotlinx.serialization.Serializable

@Serializable
data class StartPushRequest(
    var id: String,
    var stream: RequestStreamData? = null,
    var protocol: String? = null,
    var url: String? = null,
    var streamKey: String? = null
)
