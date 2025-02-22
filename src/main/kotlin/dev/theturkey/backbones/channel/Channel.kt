package dev.theturkey.backbones.channel

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object Channels : Table<Channel>("channels") {
    val id = long("id").primaryKey()
    var name = varchar("name")
    var created = varchar("created")
}

interface Channel : Entity<Channel> {
    companion object : Entity.Factory<Channel>()
    val id: Long
    var name: String
    var created: String?
}