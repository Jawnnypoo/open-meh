import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.jawnnypoo.openmeh"
    compileSdk = BuildHelper.sdkVersion()

    defaultConfig {
        applicationId = "com.jawnnypoo.openmeh"
        minSdk = 23
        targetSdk = BuildHelper.sdkVersion()
        versionCode = 202
        versionName = "2.0.2"
        buildConfigField("String", "MEH_API_KEY", BuildHelper.mehApiKey(project))
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += "META-INF/library_release.kotlin_module"
        }
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("app/${project.propertyOrEmpty("KEYSTORE_NAME")}")
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
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(platform(libs.composeBom))

    implementation(libs.kotlinxCoroutinesCore)
    implementation(libs.kotlinxCoroutinesAndroid)
    implementation(libs.kotlinxSerializationJson)

    implementation(libs.androidxCoreKtx)
    implementation(libs.androidxActivityCompose)
    implementation(libs.androidxLifecycleRuntimeKtx)
    implementation(libs.androidxLifecycleRuntimeCompose)
    implementation(libs.androidxLifecycleViewmodelCompose)

    implementation(libs.androidxComposeUi)
    implementation(libs.androidxComposeUiToolingPreview)
    implementation(libs.androidxComposeFoundation)
    implementation(libs.androidxComposeMaterial3)
    implementation(libs.androidxComposeMaterialIconsExtended)
    debugImplementation(libs.androidxComposeUiTooling)

    implementation(libs.androidxNavigation3Runtime)
    implementation(libs.androidxNavigation3Ui)

    implementation(libs.androidxBrowser)
    implementation(libs.androidxWorkRuntime)
    implementation(libs.androidxWorkRuntimeKtx)

    implementation(libs.materialComponents)
    implementation(libs.threeTenAbp)

    implementation(libs.coilCompose)
    implementation(libs.coilNetworkKtor3)

    implementation(libs.ktorClientCore)
    implementation(libs.ktorClientCio)
    implementation(libs.ktorClientContentNegotiation)
    implementation(libs.ktorSerializationKotlinxJson)
    implementation(libs.ktorClientLogging)

    implementation(libs.timber)

    implementation(libs.richtextUiMaterial3)
    implementation(libs.richtextCommonmark)

    implementation(project(":api"))
}
