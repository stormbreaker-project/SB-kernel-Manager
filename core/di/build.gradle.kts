// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

plugins {
    id("sbkm.android.library")
    id("sbkm.android.compose")
}

android {
    namespace = "dev.danascape.kernelmanager.core.di"
}

dependencies {
    api(project(":core:data"))
    api(project(":core:batterymonitor"))
    implementation(project(":core:datastore"))
    implementation(project(":core:network"))
    implementation(libs.ktor.client.okhttp)
}
