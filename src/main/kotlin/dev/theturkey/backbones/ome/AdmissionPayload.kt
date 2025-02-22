package dev.theturkey.backbones.ome

data class AdmissionPayload(
    val client: AdmissionClient,
    var request: AdmissionRequest
)
