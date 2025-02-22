package dev.theturkey.backbones.ome

data class HLSDumpData(
    var outputStreamName: String,
    var id: String,
    var outputPath: String,
    var playlist: List<String>?,
    var infoFile: String?,
    var userData: String?,
)
