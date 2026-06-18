plugins {
    alias(libs.plugins.kotlin.jvm)
}

// Test-only module: Konsist runs on the JVM by scanning the project's Kotlin source
// files, so it needs a JVM context. Nothing here is published or shipped — it exists
// purely to gate architecture rules (the pure-core purity check) in CI.

kotlin {
    jvmToolchain(21)
}

dependencies {
    testImplementation(libs.konsist)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    // Konsist scans the whole project's Kotlin sources via the filesystem, which Gradle's
    // up-to-date check cannot see (only this module's own — empty — sources are tracked).
    // Without this, a change in another module leaves a stale cached result, and a stale
    // PASS would silently hide a real architecture violation. Always re-run so the gates gate.
    outputs.upToDateWhen { false }
}
