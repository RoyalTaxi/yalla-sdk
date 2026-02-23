plugins {
    id("yalla.sdk.kmp")
}

kotlin {
    targets.withType(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget::class.java).configureEach {
        binaries.withType(org.jetbrains.kotlin.gradle.plugin.mpp.Framework::class.java).configureEach {
            export(libs.firebase.gitlive.app)
            export(libs.firebase.gitlive.analytics)
            export(libs.firebase.gitlive.crashlytics)
            export(libs.firebase.gitlive.messaging)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.firebase.gitlive.app)
            api(libs.firebase.gitlive.analytics)
            api(libs.firebase.gitlive.crashlytics)
            api(libs.firebase.gitlive.messaging)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.androidx.core.ktx)
        }
    }
}
