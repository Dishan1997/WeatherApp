plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("io.realm.kotlin")
    id("kotlin-kapt")
    id("realm-android")

}

android {
    namespace = "com.example.getlocation"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.getlocation"
        minSdk = 26
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures{
        dataBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.recyclerview:recyclerview-selection:1.1.0")
    implementation ("com.google.android.gms:play-services-location:18.0.0")

    val lifecycle_version = "2.6.1"

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata:$lifecycle_version")

    //httpUrlConnection
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.1")
    implementation("com.google.code.gson:gson:2.8.6")

    implementation ("com.google.code.gson:gson:2.8.5")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation ("com.mapbox.mapboxsdk:mapbox-android-plugin-places-v9:0.12.0")
    implementation ("com.mapbox.mapboxsdk:mapbox-android-sdk:9.6.1")
    implementation ("com.mapbox.mapboxsdk:mapbox-android-telemetry:v8.1.3")

    implementation ("androidx.work:work-runtime-ktx:2.7.1")
   //implementation("androidx.work:work-runtime-ktx:")

    implementation ("com.squareup.okhttp3:okhttp:4.9.2")

    //realm
    implementation ("io.realm.kotlin:library-base:1.11.0")
    implementation ("io.realm.kotlin:library-sync:1.11.0")// If using Device Sync
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")

    val retrofit_version = "2.8.1"

    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")

}