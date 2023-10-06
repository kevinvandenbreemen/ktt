plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "com.vandenbreemen.ktt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {

    val ktor_version = "2.3.4"
    val slf4j_version = "2.0.9"
    val markdown_version = "0.5.0"
    val coroutine_test_version = "1.7.3"
    val kluent_version = "1.73"
    val sqlite_version = "3.43.0.0"
    val sqlite_dao_version = "1.1.1.0000"

    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("org.slf4j:slf4j-api:$slf4j_version")
    implementation("org.slf4j:slf4j-simple:$slf4j_version")
    implementation("org.jetbrains:markdown:$markdown_version")

    //  Persistence
    implementation("org.xerial:sqlite-jdbc:$sqlite_version")
    implementation("com.github.kevinvandenbreemen:sqlite-dao:1.1.1.0000")

    //  Test Stuff
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutine_test_version")
    testImplementation("org.amshove.kluent:kluent:$kluent_version")


}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}