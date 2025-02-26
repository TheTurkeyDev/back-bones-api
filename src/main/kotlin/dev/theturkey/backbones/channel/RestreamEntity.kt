package dev.theturkey.backbones.channel

import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object RestreamEntity : Table<Nothing>("restreams") {
    val id = long("id").primaryKey()
    var channelId = long("channel_id")
    var name = varchar("name")
    var active = boolean("active")
    var url = varchar("url")
    var streamKey = varchar("stream_key")
    var videoTrack = varchar("video_track")
    var audioTrack = varchar("audio_track")
}