package dev.theturkey.backbones.ome
import kotlinx.serialization.Serializable

@Serializable
data class HLSDumpResponse (
    var message: String,
    var statusCode: Int,
    var response: List<HLSDumpData>? = null
)
