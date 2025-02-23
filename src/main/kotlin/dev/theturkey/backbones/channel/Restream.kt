package dev.theturkey.backbones.channel

import kotlinx.serialization.Serializable

@Serializable
data class Restream(
    val id: Long,
    var channelId: Long,
    var name: String,
    var active: Boolean,
    var url: String,
    var streamKey: String?
)