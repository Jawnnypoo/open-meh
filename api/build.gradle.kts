import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    val okhttpVersion = "4.12.0"
    val retrofitVersion = "2.11.0"

    api("com.squareup.retrofit2:retrofit:$retrofitVersion")
    api("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    api("com.squareup.moshi:moshi-kotlin:1.15.2")
    api("com.squareup.okhttp3:okhttp:$okhttpVersion")
    api("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")
}
