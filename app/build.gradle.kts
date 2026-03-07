import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("kotlin-parcelize")
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
        viewBinding = true
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
    val addendumVersion = "2.1.1"
    val workManagerVersion = "2.10.5"
    val coroutinesVersion = "1.10.2"

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.browser:browser:1.9.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.work:work-runtime:$workManagerVersion")
    implementation("androidx.work:work-runtime-ktx:$workManagerVersion")

    implementation("com.google.android.material:material:1.13.0")

    implementation("com.jakewharton.threetenabp:threetenabp:1.4.9")

    implementation("io.coil-kt:coil:2.7.0")

    implementation("com.github.Commit451:CoilImageGetter:3.0.0")

    implementation("com.atlassian.commonmark:commonmark:0.17.0")

    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("com.github.Jawnnypoo:PhysicsLayout:3.0.1")
    implementation("com.github.Jawnnypoo:CircleIndicator:1.4.1")

    implementation("com.jakewharton.timber:timber:5.0.1")

    implementation("com.github.Commit451:Easel:3.1.0")
    implementation("com.github.Commit451.Addendum:addendum:$addendumVersion")
    implementation("com.github.Commit451.Addendum:addendum-design:$addendumVersion")
    implementation("com.github.Commit451.Addendum:addendum-recyclerview:$addendumVersion")
    implementation("com.github.Commit451:Gimbal:3.0.0")
    implementation("com.github.Commit451:Alakazam:2.1.0")

    // https://github.com/blazsolar/FlowLayout/issues/31
    implementation("com.wefika:flowlayout:0.4.1") {
        exclude(group = "com.intellij", module = "annotations")
    }

    implementation("com.github.chrisbanes:PhotoView:2.3.0")

    implementation(project(":api"))
}
