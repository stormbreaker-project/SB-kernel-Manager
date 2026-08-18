// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

plugins {
    `kotlin-dsl`
}

group = "dev.danascape.kernelmanager.buildlogic"

// Matches the toolchain the daemon runs on (gradle-daemon-jvm.properties).
java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ktlint.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "sbkm.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "sbkm.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "sbkm.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "sbkm.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("jvmLibrary") {
            id = "sbkm.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("kotlinQuality") {
            id = "sbkm.kotlin.quality"
            implementationClass = "KotlinQualityConventionPlugin"
        }
    }
}
