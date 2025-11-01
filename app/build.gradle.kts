// app/build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "2.0.21-1.0.26" // Обновлённая версия, совместимая с Kotlin 2.0.21
}

android {
    namespace = "com.example.dancemusicapp"
    compileSdk = 36 // или та, что была

    defaultConfig {
        applicationId = "com.example.dancemusicapp"
        minSdk = 26
        targetSdk = 36 // или та, что была
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
}

dependencies {
    // Room
    implementation("androidx.room:room-runtime:2.6.1") // Убедимся, что версия 2.6.1
    implementation("androidx.room:room-ktx:2.6.1") // для использования с Kotlin Coroutines
    ksp("androidx.room:room-compiler:2.6.1") // KSP для генерации кода, используем функцию ksp()

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}