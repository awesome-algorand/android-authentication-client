import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

fun fetchJSON(urlStr: String): String {
    val url = URL(urlStr)
    val httpURLConnection = url.openConnection() as HttpURLConnection
    val inputStream = httpURLConnection.inputStream
    val bufferedReader = BufferedReader(InputStreamReader(inputStream))

    var resultString = ""
    var lineString: String? = ""
    while (lineString != null) {
        lineString = bufferedReader.readLine()
        resultString += lineString
    }

    bufferedReader.close()
    httpURLConnection.disconnect()

    return resultString
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "foundation.algorand.demo"
    compileSdk = 34
    configurations {
        all {
            exclude("org.bouncycastle", "bcprov-jdk15to18")
        }
    }
    defaultConfig {
        applicationId = "foundation.algorand.demo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Get all known statements from shared repository
        resValue(
            "string", "asset_statements", fetchJSON("https://raw.githubusercontent.com/awesome-algorand/registered-authenticators/main/asset_statements.json")
        )
    }
    signingConfigs {
        getByName("debug") {
            storeFile = file("../debug.jks")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(mapOf("path" to ":propagule")))

    // Algorand SDK
    implementation("com.algorand:algosdk:2.4.0")
    implementation("org.bouncycastle:bcprov-jdk15on:1.67")

    // FIDO2
    implementation("com.google.android.gms:play-services-fido:20.1.0")

    // Barcode Reader
    implementation("com.google.android.gms:play-services-code-scanner:16.1.0")

    // Kotlin Coroutine
    val coroutineVersion by extra { "1.7.1" }
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutineVersion")

    // Dagger/Hilt
    val hiltVersion by extra { "2.48" }
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    implementation("com.google.mlkit:camera:16.0.0-beta3")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.mlkit:barcode-scanning-common:17.0.0")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")
    kapt("androidx.hilt:hilt-compiler:1.1.0")

    // HTTP Requests
    val okhttpVersion by extra { "4.12.0" }
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")
    implementation("ru.gildor.coroutines:kotlin-coroutines-okhttp:1.0")

    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // Dev Dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
