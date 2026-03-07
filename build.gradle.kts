plugins {
    id("com.android.application") version "9.1.0" apply false
    id("org.jetbrains.kotlin.jvm") version "2.3.10" apply false
    id("com.github.ben-manes.versions") version "0.52.0"
}

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
