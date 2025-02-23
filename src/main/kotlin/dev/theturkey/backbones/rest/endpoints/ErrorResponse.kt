package dev.theturkey.backbones.rest.endpoints

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val message: String)