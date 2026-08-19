// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

plugins {
    id("sbkm.android.application")
    id("sbkm.android.compose")
    // :app declares MoreGraphRoute, so it needs serializers generated too.
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "dev.danascape.kernelmanager"

    defaultConfig {
        applicationId = "dev.danascape.kernelmanager"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
}

dependencies {
    // The shell owns navigation and the Application; every screen lives behind
    // a feature module.
    implementation(project(":feature:devices"))
    implementation(project(":feature:deviceinfo"))
    implementation(project(":feature:builds"))
    implementation(project(":feature:discover"))
    implementation(project(":feature:monitor"))
    implementation(project(":feature:tune"))
    implementation(project(":feature:licenses"))
    implementation(project(":feature:more"))
    implementation(project(":feature:news"))

    implementation(project(":core:designsystem"))
    implementation(project(":core:di"))
    implementation(project(":core:model"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)

    // Coil's singleton loader is configured in SBApplication.
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.svg)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
