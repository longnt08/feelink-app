plugins {
    alias(libs.plugins.android.application)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.diaryapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.diaryapp"
        minSdk = 24
        targetSdk = 34
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    val room_version = "2.6.1"
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.gson)
    implementation(libs.appcompat.v161)
    implementation(libs.circleimageview)
    implementation("androidx.emoji2:emoji2:1.5.0")
    implementation("androidx.emoji2:emoji2-views:1.5.0")
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation ("com.github.PhilJay:MPAndroidChart:3.1.0")
    implementation ("com.google.android.material:material:1.9.0")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
}