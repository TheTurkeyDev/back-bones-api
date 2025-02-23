package dev.theturkey.backbones.ome
import kotlinx.serialization.Serializable

@Serializable
data class PublishingStateResponseData (
    var id: String,
    var state: String
)
