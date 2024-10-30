plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.lab2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.lab2"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    composeOptions {
        kotlinCompilerExtensionVersion ="1.5.2"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        dataBinding = true
        viewBinding = true
        compose =true
    }
}

dependencies {
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation ("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.compose.ui:ui:1.7.3")
    implementation("androidx.compose.material3:material3-android:1.3.0")
    implementation("androidx.compose.ui:ui-tooling-preview-android:1.7.3")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.3")
    implementation("androidx.compose.ui:ui-test-junit4-android:1.7.3")
    implementation("androidx.compose.ui:ui-test-android:1.7.4")
    val fragment_version = "1.8.4"
    val room_version = "2.6.1"
    // Java language implementation
    implementation("androidx.fragment:fragment-ktx:$fragment_version")
    // Kotlin

    implementation("androidx.compose.compiler:compiler:1.5.15")
    implementation ("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")


    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:1.5.0")
    debugImplementation ("androidx.compose.ui:ui-test-manifest:1.5.0")

    val composeBom = platform("androidx.compose:compose-bom:2024.09.02")
    implementation("androidx.compose:compose-bom:2024.09.03")
    androidTestImplementation("androidx.compose:compose-bom:2024.09.03")
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.compose.material3:material3:1.1.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.0")
    implementation("androidx.activity:activity-compose:1.7.2")

    val nav_version = "2.8.2"
    //noinspection GradleDependency
    implementation("androidx.navigation:navigation-compose:$nav_version")

    implementation("com.google.firebase:firebase-auth:21.0.3")

}