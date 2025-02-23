package dev.theturkey.backbones.channel

import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object ChannelEntity : Table<Nothing>("channels") {
    val id = long("id").primaryKey()
    var name = varchar("name")
    var created = varchar("created")
}