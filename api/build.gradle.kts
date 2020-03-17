import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("kotlin")
    id("kotlin-kapt")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    val okhttpVersion = "4.4.0"
    val retrofitVersion = "2.7.2"

    api(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))

    api("com.squareup.retrofit2:retrofit:$retrofitVersion")
    api("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    api("com.squareup.moshi:moshi:${BuildHelper.moshiVersion()}")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:${BuildHelper.moshiVersion()}")
    api("com.squareup.okhttp3:okhttp:$okhttpVersion")
    api("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")
}
