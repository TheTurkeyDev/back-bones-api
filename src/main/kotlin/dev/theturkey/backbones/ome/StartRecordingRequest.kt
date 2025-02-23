package dev.theturkey.backbones.ome
import kotlinx.serialization.Serializable

@Serializable
data class StartRecordingRequest(
    var id: String,
    var stream: RequestStreamData,
    var interval: Int? = null,
    var segmentationRule: String? = null
)
