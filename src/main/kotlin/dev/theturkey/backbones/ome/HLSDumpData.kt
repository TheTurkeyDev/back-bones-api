package dev.theturkey.backbones.ome
import kotlinx.serialization.Serializable

@Serializable
data class HLSDumpData(
    var outputStreamName: String,
    var id: String,
    var outputPath: String,
    var playlist: List<String>? = null,
    var infoFile: String?,
    var userData: String?,
)
