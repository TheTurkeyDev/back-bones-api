package dev.theturkey.backbones.ome
import kotlinx.serialization.Serializable

@Serializable
data class AdmissionRequest (
	var direction: String,   	//"incoming | outgoing"
	var protocol: String,   	//"webrtc | rtmp | srt | llhls | thumbnail"
	var status: String,      	//"opening | closing"
	var url: String,
    var new_url: String? = null,
    var time: String,
)
