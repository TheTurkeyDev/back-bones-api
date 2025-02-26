package dev.theturkey.backbones.channel

import kotlinx.serialization.Serializable

@Serializable
data class Restream(
    var id: Long,
    var channelId: Long,
    var name: String,
    var active: Boolean,
    var url: String,
    var streamKey: String?,
    var videoTrack: String,
    var audioTrack: String,
)