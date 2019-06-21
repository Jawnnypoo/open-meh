buildscript {
    repositories {
        google()
        jcenter()
        maven("https://maven.fabric.io/public")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.5.0-beta04")
        classpath(kotlin("gradle-plugin", version = "1.3.40"))
        classpath("io.fabric.tools:gradle:1.29.0")
        classpath("com.google.gms:google-services:4.2.0")
    }
}

plugins {
    id("com.github.ben-manes.versions") version "0.21.0"
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
    }
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
 }
