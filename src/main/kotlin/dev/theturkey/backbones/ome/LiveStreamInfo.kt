package dev.theturkey.backbones.ome
import kotlinx.serialization.Serializable

@Serializable
data class LiveStreamInfo (
    var input: LiveStreamInfoInput,
    var name: String,
)