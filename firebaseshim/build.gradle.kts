import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdkVersion(BuildHelper.sdkVersion())

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(BuildHelper.sdkVersion())
    }
}

dependencies {
    api(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
    api("com.jakewharton.timber:timber:${BuildHelper.timberVersion()}")
    compileOnly("com.crashlytics.sdk.android:crashlytics:${BuildHelper.crashlyticsVersion()}")
}
