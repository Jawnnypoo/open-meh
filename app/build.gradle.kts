import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("io.fabric") apply false
    id("com.google.gms.google-services") apply false
}

android {

    compileSdkVersion(BuildHelper.sdkVersion())

    defaultConfig {
        applicationId = "com.jawnnypoo.openmeh"
        minSdkVersion(21)
        targetSdkVersion(BuildHelper.sdkVersion())
        versionCode = 200
        versionName = "2.0.0"
        buildConfigField("String", "MEH_API_KEY", BuildHelper.mehApiKey(project))
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        exclude("META-INF/library_release.kotlin_module")
    }

    signingConfigs {
        register("release") {
            storeFile = rootProject.file("app/${project.propertyOrEmpty("KEYSTORE_NAME")}")
            storePassword = BuildHelper.keystorePassword(project)
            keyAlias = "Jawnnypoo"
            keyPassword = BuildHelper.keyPassword(project)
        }
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"))
            signingConfig = signingConfigs.getByName("release")
        }
        named("debug") {
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"))
        }
    }

    lintOptions {
        isAbortOnError = false
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

androidExtensions {
    isExperimental = true
}

val firebaseEnabled = BuildHelper.firebaseEnabled(project)

dependencies {
    val addendumVersion = "2.1.1"
    val workManagerVersion = "2.3.0"
    val coroutinesVersion = "1.3.3"

    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    implementation("androidx.core:core-ktx:1.2.0")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.browser:browser:1.2.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.0.0")
    implementation("androidx.work:work-runtime:$workManagerVersion")
    implementation("androidx.work:work-runtime-ktx:$workManagerVersion")

    implementation("com.google.android.material:material:1.1.0")

    implementation("com.jakewharton.threetenabp:threetenabp:1.2.2")

    kapt("com.squareup.moshi:moshi-kotlin-codegen:${BuildHelper.moshiVersion()}")

    implementation("io.coil-kt:coil:0.9.5")

    implementation("com.github.Commit451:CoilImageGetter:1.0.1")

    implementation("com.atlassian.commonmark:commonmark:0.13.1")

    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("com.github.Jawnnypoo:PhysicsLayout:2.2.0")
    implementation("com.github.Jawnnypoo:CircleIndicator:1.4.0")

    implementation("com.jakewharton.timber:timber:${BuildHelper.timberVersion()}")

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

    implementation("com.github.novoda:simple-chrome-custom-tabs:0.1.6")

    implementation(project(":api"))

    implementation(project(":firebaseshim"))
    if (firebaseEnabled) {
        implementation("com.google.firebase:firebase-core:17.2.3")
        implementation("com.crashlytics.sdk.android:crashlytics:${BuildHelper.crashlyticsVersion()}")
    }
}

if (firebaseEnabled) {
    apply(mapOf("plugin" to "com.google.gms.google-services"))
    apply(mapOf("plugin" to "io.fabric"))
}

