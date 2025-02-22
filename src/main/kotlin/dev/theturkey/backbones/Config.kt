package dev.theturkey.backbones

object Config {
    val DB_URL: String = System.getenv("DB_URL") ?: ""
    val DB_USER: String = System.getenv("DB_USER") ?: ""
    val DB_PASSWORD: String = System.getenv("DB_PASSWORD") ?: ""
    val OME_IP: String = System.getenv("OME_IP") ?: ""
}


