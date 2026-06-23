import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.jawnnypoo.openmeh"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.jawnnypoo.openmeh"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
        versionCode = 202
        versionName = "2.0.2"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    packaging {
        resources {
            excludes += "META-INF/library_release.kotlin_module"
        }
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("androidApp/${project.propertyOrEmpty("KEYSTORE_NAME")}")
            storePassword = BuildHelper.keystorePassword(project)
            keyAlias = "Jawnnypoo"
            keyPassword = BuildHelper.keyPassword(project)
        }
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                )
            )
            signingConfig = signingConfigs.getByName("release")
        }
        named("debug") {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                )
            )
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(projects.shared)
}
