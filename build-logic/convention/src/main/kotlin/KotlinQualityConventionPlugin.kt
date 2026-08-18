// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jlleitschuh.gradle.ktlint.KtlintExtension

/**
 * Formatting and static analysis, applied to every module from one place.
 *
 * ktlint owns layout — it is mechanical, so it is also auto-fixable. detekt
 * owns the things a formatter cannot see, and carries the Compose rule set,
 * which catches the mistakes this codebase is actually made of: unstable
 * parameters, missing modifiers, state hoisted the wrong way.
 */
class KotlinQualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jlleitschuh.gradle.ktlint")
        pluginManager.apply("dev.detekt")

        extensions.configure<KtlintExtension> {
            version.set(libs.findVersion("ktlintEngine").get().requiredVersion)
            // Generated sources are not ours to format.
            filter {
                exclude { it.file.path.contains("/build/") }
            }
        }

        extensions.configure<DetektExtension> {
            buildUponDefaultConfig.set(true)
            config.setFrom(rootProject.file("config/detekt/detekt.yml"))
            val moduleBaseline = file("detekt-baseline.xml")
            if (moduleBaseline.exists()) baseline.set(moduleBaseline)
        }

        // Type resolution is off: it needs per-variant classpaths, roughly
        // triples the runtime, and none of the rules enabled here require it.
        tasks.withType<Detekt>().configureEach {
            reports {
                html.required.set(true)
                checkstyle.required.set(false)
                sarif.required.set(false)
                markdown.required.set(false)
            }
        }

        dependencies {
            add("detektPlugins", libs.findLibrary("detekt-composeRules").get())
        }
    }
}
