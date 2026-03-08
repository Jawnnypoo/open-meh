import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    api(libs.ktorClientCore)
    api(libs.ktorClientCio)
    api(libs.ktorClientContentNegotiation)
    api(libs.ktorSerializationKotlinxJson)
    api(libs.ktorClientLogging)
    api(libs.kotlinxSerializationJson)
}
