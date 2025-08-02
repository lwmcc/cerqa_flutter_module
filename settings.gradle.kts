pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven("https://storage.googleapis.com/download.flutter.io")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        gradlePluginPortal()

        maven("/Users/larrymccarty/AndroidStudioProjects/build/host/outputs/repo")
        maven(System.getenv("FLUTTER_STORAGE_BASE_URL") ?: "https://storage.googleapis.com/download.flutter.io")
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    val storageUrl: String = System.getenv("FLUTTER_STORAGE_BASE_URL") ?: "https://storage.googleapis.com"
    repositories {
        google()
        mavenCentral()
        maven("$storageUrl/download.flutter.io")
        maven("https://storage.googleapis.com/download.flutter.io")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven {
            url = uri("flutter-repo")
        }
    }
}


rootProject.name = "CarClub"
include(":app")

include(":flutter")
project(":flutter").projectDir = file("cerqa_flutter_module/.android")

val filePath = File(settingsDir, "cerqa_flutter_module/.android/include_flutter.groovy").path
apply(from = filePath)
