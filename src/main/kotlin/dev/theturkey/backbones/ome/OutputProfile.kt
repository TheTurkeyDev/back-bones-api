package dev.theturkey.backbones.ome

import kotlinx.serialization.Serializable

@Serializable
data class OutputProfile(
    val name: String,
    val outputStreamName: String,
    val encodes: Encodes,
)