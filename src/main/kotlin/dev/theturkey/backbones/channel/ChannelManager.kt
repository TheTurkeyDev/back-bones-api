package dev.theturkey.backbones.channel

import dev.theturkey.backbones.DatabaseCore
import dev.theturkey.backbones.ome.OMEAPI
import dev.theturkey.backbones.util.IdUtil
import dev.theturkey.backbones.util.TimeUtil
import org.ktorm.dsl.*

object ChannelManager {
    private val CHANNEL_ID_REGEX: Regex = "^[0-9]{0,9}$".toRegex()

    fun isValidId(id: String?): Boolean {
        return id?.matches(CHANNEL_ID_REGEX) ?: false
    }

    fun getChannels(): List<Channel> {
        return DatabaseCore.DB
            .from(ChannelEntity)
            .select()
            .map {
                Channel(it[ChannelEntity.id]!!, it[ChannelEntity.name]!!, it[ChannelEntity.created]!!)
            }.toList()
    }

    fun getChannel(id: Long): Channel? {
        return DatabaseCore.DB
            .from(ChannelEntity)
            .select()
            .where(ChannelEntity.id.eq(id))
            .map {
                Channel(it[ChannelEntity.id]!!, it[ChannelEntity.name]!!, it[ChannelEntity.created]!!)
            }
            .firstOrNull()
    }

    fun createChannel(channel: Channel): Long {
        val channelId = DatabaseCore.DB.insert(ChannelEntity) {
            set(it.name, channel.name)
            set(it.created, TimeUtil.nowStr())
        }.toLong()

        setChannelStreamKey(channelId, IdUtil.randomUID(32))

        return channelId;
    }

    fun updateChannel(channel: Channel) {
        DatabaseCore.DB.update(ChannelEntity) {
            set(it.name, channel.name)
            where { it.id eq channel.id }
        }
    }

    fun deleteChannel(id: Long) {
        DatabaseCore.DB.delete(ChannelEntity) { it.id eq id }
    }

    fun getChannelStreamKey(channelId: Long): ChannelStreamKey? {
        return DatabaseCore.DB.from(ChannelStreamKeyEntity)
            .select()
            .where(ChannelStreamKeyEntity.channelId.eq(channelId))
            .map { ChannelStreamKey(it[ChannelStreamKeyEntity.streamKey]!!, it[ChannelStreamKeyEntity.channelId]!!) }
            .firstOrNull()
    }

    fun setChannelStreamKey(channelId: Long, key: String?) {
        DatabaseCore.DB.update(ChannelStreamKeyEntity) {
            set(it.streamKey, key)
            where { it.channelId eq channelId }
        }
    }

    fun getChannelFromStreamKey(key: String): Long? {
        return DatabaseCore.DB.from(ChannelStreamKeyEntity)
            .select()
            .where(ChannelStreamKeyEntity.streamKey.eq(key))
            .map { it[ChannelStreamKeyEntity.channelId] }
            .firstOrNull()
    }

    fun getRestreams(): List<Restream> {
        return DatabaseCore.DB.from(RestreamEntity)
            .select()
            .map {
                Restream(
                    it[RestreamEntity.id]!!,
                    it[RestreamEntity.channelId]!!,
                    it[RestreamEntity.name]!!,
                    it[RestreamEntity.active]!!,
                    it[RestreamEntity.url]!!,
                    it[RestreamEntity.streamKey],
                )
            }
            .toList()
    }

    fun getRestreamsForChannel(id: Long): List<Restream> {
        return DatabaseCore.DB.from(RestreamEntity)
            .select()
            .where(RestreamEntity.channelId.eq(id))
            .map {
                Restream(
                    it[RestreamEntity.id]!!,
                    it[RestreamEntity.channelId]!!,
                    it[RestreamEntity.name]!!,
                    it[RestreamEntity.active]!!,
                    it[RestreamEntity.url]!!,
                    it[RestreamEntity.streamKey],
                )
            }
            .toList()
    }

    fun getRestream(id: Long): Restream? {
        return DatabaseCore.DB.from(RestreamEntity)
            .select()
            .where(RestreamEntity.id.eq(id))
            .map {
                Restream(
                    it[RestreamEntity.id]!!,
                    it[RestreamEntity.channelId]!!,
                    it[RestreamEntity.name]!!,
                    it[RestreamEntity.active]!!,
                    it[RestreamEntity.url]!!,
                    it[RestreamEntity.streamKey],
                )
            }
            .firstOrNull()
    }

    fun addRestreamsForChannel(restream: Restream) {
        DatabaseCore.DB.insert(RestreamEntity) {
            set(it.channelId, restream.channelId)
            set(it.name, restream.name)
            set(it.active, restream.active)
            set(it.url, restream.url)
            set(it.streamKey, restream.streamKey)
        }
    }

    fun updateRestream(restream: Restream) {
        DatabaseCore.DB.update(RestreamEntity) {
            set(it.name, restream.name)
            set(it.active, restream.active)
            set(it.url, restream.url)
            set(it.streamKey, restream.streamKey)
            where { it.id eq restream.id }
        }
    }

    fun deleteRestream(id: Long) {
        DatabaseCore.DB.delete(RestreamEntity) { it.id eq id }
    }


    fun startRestreams() {
        for (r in getRestreams()) {
            if (!r.active)
                continue
            if (!OMEAPI.startPushPublish(r))
                println("Filed to start restream " + r.id + "!")
        }
    }

    fun stopRestreams() {
        for (r in getRestreams()) {
            if (!r.active)
                continue
            if (!OMEAPI.stopPushPublish(r))
                println("Filed to stop restream " + r.id + "!")
        }
    }
}
