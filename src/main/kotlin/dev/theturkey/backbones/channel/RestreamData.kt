package dev.theturkey.backbones.channel

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object Restreams : Table<Restream>("restream") {
    val id = long("id").primaryKey()
    var channelId = long("channel_id")
    var name = varchar("name")
    var active = boolean("active")
    var url = varchar("url")
    var streamKey = varchar("stream_key")
}

interface Restream : Entity<Restream> {
    companion object : Entity.Factory<Restream>()

    val id: Long
    var channelId: Long
    var name: String
    var active: Boolean
    var url: String
    var streamKey: String?
}
