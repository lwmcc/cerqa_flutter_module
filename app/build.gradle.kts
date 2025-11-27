plugins {
    kotlin("multiplatform") version "2.2.0" apply false
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0"
    id("com.google.gms.google-services")
    id("io.gitlab.arturbosch.detekt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.mccartycarclub"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mccartycarclub"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.mccartycarclub.CustomTestRunner"

        testInstrumentationRunnerArguments["grantPermission"] = "true"

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    packaging {
        resources.excludes.addAll(
            listOf(
                "/META-INF/LICENSE.md",
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
            )
        )
    }
}

dependencies {

    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.runner)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.ui.test.junit4.android)
    implementation(libs.androidx.navigation.testing.android)
    implementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.navigation.testing)

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2025.01.01")
    implementation(composeBom)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.animation)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation( libs.hilt.android)

    // Testing
    testImplementation(composeBom)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.robolectric)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    debugImplementation(composeBom)
    debugImplementation(libs.androidx.ui.test.manifest)
    kaptTest(libs.hilt.android.compiler)
    kaptAndroidTest(libs.hilt.android.compiler)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.uiautomator)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.mockk.android)
    debugImplementation(libs.androidx.ui.test.manifest)

    kaptAndroidTest(libs.hilt.compiler)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt (libs.hilt.compiler)

    implementation(libs.material)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.material3)

    kaptAndroidTest(libs.hilt.android.compiler)

    // AWS Amplify
    implementation(libs.authenticator)
    implementation(libs.aws.api)
    implementation(libs.authenticator.vandroidauthenticatorversion)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.core)
    implementation(libs.core.kotlin)
    implementation(libs.aws.datastore)

    // AWS AppSync
    implementation(libs.aws.android.sdk.appsync)

    // Moshi
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapters)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Ably
    implementation(libs.ably.android)
    implementation("io.ably:ably-android:1.2.20")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.core)

    implementation(libs.androidx.datastore.preferences)

    // Google phone number validator
    implementation(libs.libphonenumber)

    implementation("androidx.compose.runtime:runtime:1.6.0")

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    implementation(project(":flutter"))
    debugImplementation("io.flutter:flutter_embedding_debug:1.0.0-<hash>")
    releaseImplementation("io.flutter:flutter_embedding_release:1.0.0-<hash>")
    implementation(project(":shared"))
}

kapt {
    correctErrorTypes = true
}

detekt {
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    buildUponDefaultConfig = false
}