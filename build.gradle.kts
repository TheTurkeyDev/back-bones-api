plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    application
}

var buildNumber: Int = System.getenv("BUILD_NUMBER")?.toInt() ?: 0

group = "dev.theturkey.backbones"
version = "0.1.${buildNumber}"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.glassfish.jersey.core:jersey-server:3.1.0")
    implementation("org.glassfish.jersey.containers:jersey-container-servlet:3.1.0")
    implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-http:3.1.0")
    implementation("org.glassfish.jersey.inject:jersey-hk2:3.1.0")
    implementation("jakarta.activation:jakarta.activation-api:2.1.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.1.4")
    implementation("org.ktorm:ktorm-core:4.1.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.getByName<Tar>("distTar").archiveFileName = "${project.name}.tar"

application {
    mainClass = "dev.theturkey.backbones.MainKt"
}