// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

plugins {
    id("sbkm.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "dev.danascape.kernelmanager.core.network"

    buildFeatures { buildConfig = true }

    defaultConfig {
        // Kept out of source so a debug build can be pointed at a staging deploy.
        buildConfigField("String", "API_BASE_URL", "\"https://stormbreaker.squadri.me\"")
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
}
