package dev.theturkey.backbones.ome
import kotlinx.serialization.Serializable

@Serializable
data class RequestStreamData(
    var name: String,
    var variantNames: List<String>? = null
)
