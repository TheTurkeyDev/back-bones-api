package dev.theturkey.backbones

import org.ktorm.database.Database

object DatabaseCore {
    val DB = Database.connect(
        url = Config.DB_URL,
        driver = "org.mariadb.jdbc.Driver",
        user = Config.DB_USER,
        password = Config.DB_PASSWORD
    )
}
