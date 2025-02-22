package dev.theturkey.backbones.channel

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object ChannelStreamKeys : Table<ChannelStreamKey>("stream_keys") {
    var streamKey = varchar("stream_key").primaryKey()
    var channelId = long("channel_id")
}

interface ChannelStreamKey : Entity<ChannelStreamKey> {
    companion object : Entity.Factory<ChannelStreamKey>()

    val streamKey: String
    var channelId: Long
}
