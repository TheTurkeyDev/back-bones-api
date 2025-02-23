package dev.theturkey.backbones.ome
import kotlinx.serialization.Serializable

@Serializable
data class AdmissionPayload(
    val client: AdmissionClient,
    var request: AdmissionRequest
)
