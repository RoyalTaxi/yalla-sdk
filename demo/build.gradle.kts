plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "uz.yalla.sdk.demo"
    compileSdk = 36

    defaultConfig {
        applicationId = "uz.yalla.sdk.demo"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
