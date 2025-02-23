package dev.theturkey.backbones.ome

import kotlinx.serialization.Serializable

@Serializable
data class AdmissionClient(
    var address: String,
    var port: Int,
    var real_ip: String,
    var user_agent: String? = null
)
