package dev.theturkey.backbones.ome
import kotlinx.serialization.Serializable

@Serializable
data class LiveStreamInfoInput (
    var createdTime: String,
    var sourceType: String
)
