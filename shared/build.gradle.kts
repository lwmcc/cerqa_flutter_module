plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0"
    id("org.jetbrains.compose") version "1.8.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.0"
    id("com.apollographql.apollo") version "4.1.0"
    kotlin("native.cocoapods")
}

kotlin {
    // Suppress expect/actual classes warning
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.cerqa.shared"
        compileSdk = 36
        minSdk = 29

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    val xcfName = "Shared"

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = xcfName
            isStatic = true
        }
    }

    cocoapods {
        summary = "Shared KMP module"
        homepage = "https://example.com"
        version = "1.0"
        ios.deploymentTarget = "16.0"

        framework {
            baseName = xcfName
            isStatic = true
        }
    }

    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.components.resources)
            }
        }

        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.lifecycle.viewmodel.compose)
                implementation(libs.navigation.compose)
                implementation(compose.materialIconsExtended)
                implementation(compose.material3)
                implementation(libs.koin.core)
                implementation("io.insert-koin:koin-compose:4.1.1")
                implementation("io.insert-koin:koin-compose-viewmodel:4.1.1")

                // Ktor client for API calls
                implementation("io.ktor:ktor-client-core:3.3.2")
                implementation("io.ktor:ktor-client-content-negotiation:3.3.2")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.3.2")
                implementation("io.ktor:ktor-client-auth:3.3.2")
                implementation("io.ktor:ktor-client-logging:3.3.2")

                // Apollo GraphQL client
                implementation("com.apollographql.apollo:apollo-runtime:4.1.0")
                implementation("com.apollographql.apollo:apollo-normalized-cache:4.1.0")

                // Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

                // kotlinx.coroutines will be available in all source sets
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

                // Coil for image loading (supports KMP)
                implementation("io.coil-kt.coil3:coil-compose:3.0.4")
                implementation("io.coil-kt.coil3:coil-network-ktor3:3.0.4")
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                // Add Android-specific dependencies here. Note that this source set depends on
                // commonMain by default and will correctly pull the Android artifacts of any KMP
                // dependencies declared in commonMain.
                implementation(libs.androidx.ui)
                implementation(libs.androidx.material)
                implementation(libs.androidx.ui.tooling.preview)
                implementation(libs.material3)
                implementation(libs.androidx.material3.adaptive)
                implementation(libs.koin.android)
                implementation(libs.androidx.core.ktx)
                implementation(libs.koin.androidx.compose)

                // Ktor Android engine
                implementation("io.ktor:ktor-client-okhttp:3.3.2")

                // Amplify Android SDK for authentication
                implementation("com.amplifyframework:core:2.14.+")
                implementation("com.amplifyframework:aws-auth-cognito:2.14.+")

                // Ably Android SDK
                implementation("io.ably:ably-android:1.2.48")
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.junit)
            }
        }

        iosMain {
            dependencies {
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP's default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.
                kotlin("multiplatform")
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)

                // Ktor iOS engine
                implementation("io.ktor:ktor-client-darwin:3.3.2")
            }
        }
    }

    tasks.matching { it.name == "syncComposeResourcesForIos" }.configureEach {
        enabled = false // Disable this task - we'll handle resources differently
    }

}

apollo {
    service("cerqa") {
        packageName.set("com.cerqa.graphql")

        // Configure Apollo to use Ktor's HTTP engine
        generateKotlinModels.set(true)

        // Enable normalized cache support
        generateApolloMetadata.set(true)

        // AppSync-specific configuration
        // Only add __typename when explicitly requested in queries
        // Setting to "ifPolymorphic" instead of "always" to avoid issues with Lambda responses
        addTypename.set("ifPolymorphic")

        // Schema location - you'll download this from AppSync
        schemaFile.set(file("src/commonMain/graphql/schema.graphqls"))

        // Where to find your GraphQL queries/mutations/subscriptions
        srcDir("src/commonMain/graphql")

        // Map AWS custom scalars to Kotlin types
        mapScalar("AWSDate", "kotlin.String")
        mapScalar("AWSTime", "kotlin.String")
        mapScalar("AWSDateTime", "kotlin.String")
        mapScalar("AWSTimestamp", "kotlin.Long")
        mapScalar("AWSEmail", "kotlin.String")
        mapScalar("AWSJSON", "kotlin.String")
        mapScalar("AWSURL", "kotlin.String")
        mapScalar("AWSPhone", "kotlin.String")
        mapScalar("AWSIPAddress", "kotlin.String")
    }
}