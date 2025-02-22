package dev.theturkey.backbones.ome

data class AdmissionRequest (
	var direction: String,   	//"incoming | outgoing"
	var protocol: String,   	//"webrtc | rtmp | srt | llhls | thumbnail"
	var status: String,      	//"opening | closing"
	var url: String,
    var new_url: String?,    	//"opening | closing"
    var time: String,        	//"opening | closing"
)
