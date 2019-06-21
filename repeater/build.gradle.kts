import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdkVersion(BuildHelper.sdkVersion())


    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(BuildHelper.sdkVersion())
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    api(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
    api("androidx.core:core-ktx:1.0.2")
}
