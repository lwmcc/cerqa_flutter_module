pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        // Flutter disabled
        // maven("https://storage.googleapis.com/download.flutter.io")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        gradlePluginPortal()

        // Flutter disabled
        // maven("/Users/larrymccarty/AndroidStudioProjects/build/host/outputs/repo")
        // maven(System.getenv("FLUTTER_STORAGE_BASE_URL") ?: "https://storage.googleapis.com/download.flutter.io")
        // gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    // Flutter disabled
    // val storageUrl: String = System.getenv("FLUTTER_STORAGE_BASE_URL") ?: "https://storage.googleapis.com"
    repositories {
        google()
        mavenCentral()
        // Flutter disabled
        // maven("$storageUrl/download.flutter.io")
        // maven("https://storage.googleapis.com/download.flutter.io")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        // Flutter disabled
        // maven {
        //     url = uri("flutter-repo")
        // }
    }
}


rootProject.name = "CarClub"
include(":app")
include(":shared")

// Flutter disabled
// include(":flutter")
// project(":flutter").projectDir = file("cerqa_flutter_module/.android")
//
// val filePath = File(settingsDir, "cerqa_flutter_module/.android/include_flutter.groovy").path
// apply(from = filePath)
