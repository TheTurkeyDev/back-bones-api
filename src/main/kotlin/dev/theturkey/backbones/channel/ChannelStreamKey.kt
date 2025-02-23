package dev.theturkey.backbones.channel

import kotlinx.serialization.Serializable

@Serializable
data class ChannelStreamKey(
    var streamKey: String,
    val channelId: Long
)