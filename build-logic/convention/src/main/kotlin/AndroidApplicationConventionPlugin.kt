// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")

        extensions.configure<ApplicationExtension> {
            compileSdk {
                version = release(SdkVersions.COMPILE) {
                    minorApiLevel = SdkVersions.COMPILE_MINOR
                }
            }
            val version = SemanticVersion.parse(libs.findVersion("app").get().requiredVersion)

            defaultConfig {
                minSdk = SdkVersions.MIN
                targetSdk = SdkVersions.TARGET
                versionName = version.name
                versionCode = version.code
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }
        }
    }
}
