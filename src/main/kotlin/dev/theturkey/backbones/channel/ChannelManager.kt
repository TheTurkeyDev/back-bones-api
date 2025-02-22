package dev.theturkey.backbones.channel

import dev.theturkey.backbones.DatabaseCore
import dev.theturkey.backbones.DatabaseCore.channels
import dev.theturkey.backbones.DatabaseCore.restreams
import dev.theturkey.backbones.DatabaseCore.streamKeys
import dev.theturkey.backbones.ome.OMEAPI
import org.ktorm.dsl.*
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.toList
import kotlin.text.insert

object ChannelManager {
    private val CHANNEL_ID_REGEX: Regex = "^[0-9]{0,9}$".toRegex()

    fun isValidId(id: String?): Boolean {
        return id?.matches(CHANNEL_ID_REGEX) ?: false
    }

    fun getChannel(id: Long): Channel? {
        return DatabaseCore.DB.channels.find { it.id eq id }
    }

    fun createChannel(channel: Channel) {
        DatabaseCore.DB.insert(Channels) {
            set(it.name, channel.name)
            set(it.created, channel.created)
        }
    }

    fun updateChannel(channel: Channel) {
        DatabaseCore.DB.update(Channels) {
            set(it.name, channel.name)
            where { it.id eq channel.id }
        }
    }

    fun deleteChannel(id: Long) {
        DatabaseCore.DB.delete(Channels) { it.id eq id }
    }

    fun getChannelStreamKey(channelId: Long): String? {
        return DatabaseCore.DB.streamKeys.find { it.channelId eq channelId }?.streamKey
    }

    fun setChannelStreamKey(channelId: Long, key: String?) {
        DatabaseCore.DB.update(ChannelStreamKeys) {
            set(it.streamKey, key)
            where { it.channelId eq channelId }
        }
    }

    fun getChannelFromStreamKey(key: String): Long {
        return DatabaseCore.DB.streamKeys.find { it.streamKey eq key }?.channelId ?: -1
    }


    fun getRestreamsForChannel(id: Long): List<Restream> {
        return DatabaseCore.DB.restreams.filter { it.channelId eq id }.toList()
    }

    fun getRestream(id: Long): Restream? {
        return DatabaseCore.DB.restreams.find { it.id eq id }
    }

    fun addRestreamsForChannel(restream: Restream) {
        DatabaseCore.DB.insert(Restreams) {
            set(it.channelId, restream.channelId)
            set(it.name, restream.name)
            set(it.active, restream.active)
            set(it.url, restream.url)
            set(it.streamKey, restream.streamKey)
        }
    }

    fun updateRestream(restream: Restream) {
        DatabaseCore.DB.update(Restreams) {
            set(it.name, restream.name)
            set(it.active, restream.active)
            set(it.url, restream.url)
            set(it.streamKey, restream.streamKey)
            where { it.id eq restream.id }
        }
    }

    fun deleteRestream(id: Long) {
        DatabaseCore.DB.delete(Restreams) { it.id eq id }
    }

    fun startRestreams() {
        for (r in DatabaseCore.DB.restreams) {
            if (!r.active)
                continue
            if (!OMEAPI.startPushPublish(r))
                println("Filed to start restream " + r.id + "!")
        }
    }

    fun stopRestreams() {
        for (r in DatabaseCore.DB.restreams) {
            if (!r.active)
                continue
            if (!OMEAPI.stopPushPublish(r))
                println("Filed to stop restream " + r.id + "!")
        }
    }
}
