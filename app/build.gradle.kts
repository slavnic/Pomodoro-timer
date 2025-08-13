plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.pomodorotimer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pomodorotimer"
        minSdk = 24  // Umesto 34, za bolju kompatibilnost
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation("com.google.android.gms:play-services-base:18.2.0")
    implementation("com.github.iwgang:countdownview:2.1.6")
    implementation("com.daimajia.numberprogressbar:library:1.4@aar")
    implementation ("androidx.appcompat:appcompat:1.6.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}