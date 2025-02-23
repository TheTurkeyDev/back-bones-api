package dev.theturkey.backbones.channel

import kotlinx.serialization.Serializable

@Serializable
data class Channel(
    val id: Long,
    var name: String,
    var created: String
)