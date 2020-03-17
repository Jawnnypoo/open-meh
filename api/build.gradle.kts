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
    val retrofitVersion = "2.7.1"
    val moshiVersion = "1.9.2"

    api(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))

    api("com.squareup.retrofit2:retrofit:$retrofitVersion")
    api("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    api("com.squareup.retrofit2:adapter-rxjava2:$retrofitVersion")
    api("com.squareup.moshi:moshi:$moshiVersion")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")
    api("com.squareup.okhttp3:okhttp:$okhttpVersion")
    api("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")

    api("io.reactivex.rxjava2:rxjava:2.2.19")
}
