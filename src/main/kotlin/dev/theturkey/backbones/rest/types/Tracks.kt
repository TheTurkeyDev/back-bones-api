package dev.theturkey.backbones.rest.types

import kotlinx.serialization.Serializable

@Serializable
data class Tracks(
    var video: List<String>,
    var audio: List<String>
)
