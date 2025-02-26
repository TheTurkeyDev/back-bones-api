package dev.theturkey.backbones.ome

import kotlinx.serialization.Serializable

@Serializable
data class OutputProfileInformationResponse(
    var message: String,
    var statusCode: Int,
    var response: OutputProfile? = null
)