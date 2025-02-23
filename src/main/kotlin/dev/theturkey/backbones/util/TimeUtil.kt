package dev.theturkey.backbones.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object TimeUtil {
    val UTC_ZONE: ZoneId = ZoneId.of("GMT")
    val ET_ZONE: ZoneId = ZoneId.of("America/New_York")
    val FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT.withZone(UTC_ZONE)
    val BASE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun now(): ZonedDateTime {
        val zdt = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault())
        return zdt.withZoneSameInstant(UTC_ZONE)
    }

    fun diffFromNowMillis(zdt: ZonedDateTime): Long {
        return ChronoUnit.MILLIS.between(zdt, now())
    }

    fun nowStr(): String {
        return FORMATTER.format(now())
    }

    fun StringToDate(str: String): ZonedDateTime {
        return ZonedDateTime.parse(str, FORMATTER)
    }
}
