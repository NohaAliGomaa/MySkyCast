plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization")version "2.1.10"
}

android {
    namespace = "com.example.skycast"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.skycast"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation( "androidx.activity:activity-compose:1.7.2")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation ("androidx.compose.material3:material3:1.1.1")
    implementation( "com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("io.coil-kt:coil-compose:2.4.0")
    implementation( "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation( "androidx.work:work-runtime-ktx:2.8.1")
// Compose
    implementation( "androidx.compose.ui:ui:1.6.0")
    implementation ("androidx.compose.material3:material3:1.2.0")
    implementation ("androidx.activity:activity-compose:1.8.0")
    implementation( "androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation( "androidx.navigation:navigation-compose:2.7.5")
    implementation( "androidx.lifecycle:lifecycle-runtime-compose:2.7.0")


// Retrofit
    implementation( "com.squareup.retrofit2:retrofit:2.9.0")
    implementation( "com.squareup.retrofit2:converter-gson:2.9.0")

// Coroutines
    implementation( "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

// Coil for image loading
    implementation ("io.coil-kt:coil-compose:2.5.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose-android:2.8.7")
    //Room
    val room_version = "2.6.0"
    implementation("androidx.room:room-runtime:$room_version")
    // Kotlin Symbol Processing (KSP)
    ksp("androidx.room:room-compiler:$room_version")
// optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")
//Glide
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
    // Coil for image loading
    implementation ("io.coil-kt:coil-compose:2.5.0")
    //serilization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

// Lottie Compose
    val lottieVersion = "6.0.0"
    implementation("com.airbnb.android:lottie-compose:$lottieVersion")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:1.6.1")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.material3:material3:1.2.0-alpha08")


            // or latest stable

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("io.coil-kt:coil-compose:2.5.0") // Image loading for Compose

    // Optional:
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation ("com.google.code.gson:gson:2.10.1") // or latest version



    //Room
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

//    val nav_version = 2.8.8
    implementation (libs.androidx.navigation.compose)
    implementation (libs.kotlinx.serialization.json)

    implementation(libs.accompanist.systemuicontroller)

    implementation(libs.lottie.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}