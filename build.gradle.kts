// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    extra["kotlin_version"] = "1.6.20"
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven(
                "https://plugins.gradle.org/m2/"
        )
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.0.2")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.20")
        classpath ("gradle.plugin.com.cookpad.android.plugin:plugin:1.2.5")
    }

}
plugins {
    id("io.gitlab.arturbosch.detekt") version "1.18.1"
}
allprojects {
    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
    }
    apply(from = "$rootDir/detekt/detekt.gradle")
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
}

apply(plugin = "com.cookpad.android.plugin.license-tools")
