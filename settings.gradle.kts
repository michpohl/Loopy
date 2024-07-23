pluginManagement {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven(
            "https://plugins.gradle.org/m2/"
        )
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven("https://jitpack.io")
        maven(
            "https://plugins.gradle.org/m2/"
        )
    }
}

include(":adapter")
include(":app")
include(":service")
include(":shared")
include(":player")
