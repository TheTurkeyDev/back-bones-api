package dev.theturkey.backbones.channel

import kotlinx.serialization.Serializable

@Serializable
data class Channel(
    var id: Long,
    var name: String,
    var created: String
)