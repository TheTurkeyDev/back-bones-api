package dev.theturkey.backbones

import dev.theturkey.backbones.channel.ChannelStreamKeys
import dev.theturkey.backbones.channel.Channels
import dev.theturkey.backbones.channel.Restreams
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf

object DatabaseCore {
    val DB = Database.connect(Config.DB_URL, user = Config.DB_USER, password = Config.DB_PASSWORD)
    val Database.channels get() = this.sequenceOf(Channels)
    val Database.restreams get() = this.sequenceOf(Restreams)
    val Database.streamKeys get() = this.sequenceOf(ChannelStreamKeys)
}
