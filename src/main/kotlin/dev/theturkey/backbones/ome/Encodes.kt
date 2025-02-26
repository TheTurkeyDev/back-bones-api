package dev.theturkey.backbones.ome

import kotlinx.serialization.Serializable

@Serializable
data class Encodes(
    var audios: List<AudioProfile>,
    var videos: List<VideoProfile>,
)
