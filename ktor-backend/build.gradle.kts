//val ktor_version: String by project
//val kotlin_version: String by project
//val logback_version: String by project
//
//plugins {
//    kotlin("jvm") version "1.9.10"
//    id("io.ktor.plugin") version "2.3.5"
//    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
//}
//kotlin {
//    jvmToolchain(17)
//}
//group = "edu.msd"
//version = "0.0.1"
//
//application {
//    mainClass.set("edu.msd.ApplicationKt")
//
//    val isDevelopment: Boolean = project.ext.has("development")
//    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
//}
//
//repositories {
//    mavenCentral()
//}
//
//dependencies {
//    implementation("io.ktor:ktor-server-core-jvm")
//    implementation("io.ktor:ktor-server-sessions-jvm")
//    implementation("io.ktor:ktor-server-compression-jvm")
//    implementation("io.ktor:ktor-server-call-logging-jvm")
//    implementation("io.ktor:ktor-server-content-negotiation-jvm")
//    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
//    implementation("io.ktor:ktor-server-netty-jvm")
//    implementation("ch.qos.logback:logback-classic:$logback_version")
//    testImplementation("io.ktor:ktor-server-tests-jvm")
//    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
//
//    implementation("io.ktor:ktor-server-core:2.0.0")
//    implementation("io.ktor:ktor-server-netty:2.0.0")
//    implementation("io.ktor:ktor-server-status-pages:2.0.0")
//    implementation("io.ktor:ktor-serialization-gson:2.0.0")
//    implementation("org.jetbrains.exposed:exposed-core:0.36.2")
//    implementation("org.jetbrains.exposed:exposed-dao:0.36.2")
//    implementation("org.jetbrains.exposed:exposed-jdbc:0.36.2")
//    implementation("com.h2database:h2:1.4.200")
//
//
//    //for DB stuff
//    implementation("org.jetbrains.exposed:exposed-core:0.44.0")
//    implementation("org.jetbrains.exposed:exposed-dao:0.44.0")
//    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.0")
//    implementation("com.h2database:h2:2.1.214")
//
//    implementation("io.ktor:ktor-server-resources:$ktor_version")
//
//    implementation("io.ktor:ktor-serialization-kotlinx-xml:$ktor_version")
//    implementation("org.slf4j:slf4j-simple:1.7.36")
//}
val ktor_version = "2.3.5"
val kotlin_version = "1.9.10"
val logback_version = "1.2.11" // 你可以根据需要更新这个版本

plugins {
    kotlin("jvm") version "1.9.10"
    id("io.ktor.plugin") version "2.3.5"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
}

kotlin {
    jvmToolchain(17)
}

group = "edu.msd"
version = "0.0.1"

application {
    mainClass.set("edu.msd.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor dependencies
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-sessions:$ktor_version")
    implementation("io.ktor:ktor-server-compression:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")
    implementation("io.ktor:ktor-server-resources:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-xml:$ktor_version")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.slf4j:slf4j-simple:1.7.36")

    // Database (Exposed and H2)
    implementation("org.jetbrains.exposed:exposed-core:0.44.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.0")
    implementation("com.h2database:h2:2.1.214")

    // Testing
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
