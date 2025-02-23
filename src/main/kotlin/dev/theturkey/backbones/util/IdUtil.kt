package dev.theturkey.backbones.util

import java.util.*

object IdUtil {
    private val rand: Random = Random()

    private val CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray()

    fun randomUID(length: Int = 8): String {
        val builder = StringBuilder()
        for (i in 0..<length)
            builder.append(CHARACTERS[rand.nextInt(CHARACTERS.size)])
        return builder.toString()
    }
}
