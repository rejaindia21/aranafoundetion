// ============================================================
// ARNA GRAM UNNAYA FOUNDATION
// Top-Level Gradle Build Configuration
// ============================================================

plugins {

    // Android Application
    alias(libs.plugins.android.application) apply false

    // Kotlin Compose
    alias(libs.plugins.kotlin.compose) apply false

    // Kotlin Symbol Processing
    alias(libs.plugins.google.devtools.ksp) apply false

    // Roborazzi - Screenshot/UI Testing
    alias(libs.plugins.roborazzi) apply false

    // Secrets Gradle Plugin
    alias(libs.plugins.secrets) apply false

    // Google Services
    alias(libs.plugins.google.services) apply false
}