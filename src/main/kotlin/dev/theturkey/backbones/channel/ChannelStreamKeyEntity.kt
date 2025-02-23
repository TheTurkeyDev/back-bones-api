package dev.theturkey.backbones.channel

import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object ChannelStreamKeyEntity : Table<Nothing>("stream_keys") {
    var streamKey = varchar("stream_key").primaryKey()
    var channelId = long("channel_id")
}