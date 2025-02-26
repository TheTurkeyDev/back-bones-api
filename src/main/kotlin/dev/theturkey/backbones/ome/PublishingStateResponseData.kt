package dev.theturkey.backbones.ome

import kotlinx.serialization.Serializable

@Serializable
data class PublishingStateResponseData(
    var id: String,
    var state: String,
    var vhost: String,
    var app: String,
//"stream": {
//    "name": "{output_stream_name}",
//    "trackIds": [],
//    "variantNames": []
//},

    var protocol: String,
    var url: String,
    var streamKey: String,

    var sentBytes: Long,
    var sentTime: Long,
    var sequence: Long,
    var totalsentBytes: Long,
    var totalsentTime: Long,

    var createdTime: String,
    var startTime: String,
    var finishTime: String
)
