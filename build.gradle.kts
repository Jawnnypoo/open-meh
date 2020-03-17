buildscript {
    repositories {
        google()
        jcenter()
        maven("https://maven.fabric.io/public")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.6.1")
        classpath(kotlin("gradle-plugin", version = "1.3.70"))
        classpath("io.fabric.tools:gradle:1.31.2")
        classpath("com.google.gms:google-services:4.3.3")
    }
}

plugins {
    id("com.github.ben-manes.versions") version "0.28.0"
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
