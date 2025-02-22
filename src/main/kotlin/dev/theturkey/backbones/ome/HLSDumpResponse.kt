package dev.theturkey.backbones.ome

data class HLSDumpResponse (
    var message: String,
    var statusCode: Int,
    var response: List<HLSDumpData>?
)
