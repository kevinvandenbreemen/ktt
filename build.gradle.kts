plugins {
    kotlin("jvm") version "1.8.21"
    application
    java
    `maven-publish`
    `java-library`
}

group = "com.vandenbreemen.ktt"
version = "1.0.0"

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
    val sqlite_dao_version = "1.1.2.0002"
    val kotlin_html_version = "0.9.1"
    val kmarkdown_tools_version = "1.0.0.0"

    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("org.slf4j:slf4j-api:$slf4j_version")
    implementation("org.slf4j:slf4j-simple:$slf4j_version")
    implementation("org.jetbrains:markdown:$markdown_version")

    //  Persistence
    implementation("com.github.kevinvandenbreemen:sqlite-dao:$sqlite_dao_version")

    //  HTML
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlin_html_version")

    //  Markdown tooling
    implementation("com.github.kevinvandenbreemen:kmarkdown-tools:$kmarkdown_tools_version")

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

val fatJar = task("FatJar", type = Jar::class) {

    val jarName = "ktt.jar"

    archiveFileName.set(jarName)
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes["Main-Class"] = "com.vandenbreemen.ktt.main.WikiApplication"

    }
    from(configurations.runtimeClasspath.get().map {
        if(it.isDirectory) it else zipTree(it)
    })
    with(tasks.jar.get() as CopySpec)

    copy {
        from("build/libs/$jarName")
        into("./")
    }
    println("Built and copied $jarName")
}

//  Based on https://github.com/gradle/kotlin-dsl-samples/blob/master/samples/maven-publish/build.gradle.kts
val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}