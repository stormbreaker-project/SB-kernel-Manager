// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.designsystem.component

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

/** Opens an article on the website in a Custom Tab. */
fun Context.openArticle(
    url: String,
    toolbarColor: Int,
) {
    val uri = url.toUri()
    val colors =
        CustomTabColorSchemeParams
            .Builder()
            .setToolbarColor(toolbarColor)
            .build()

    val customTab =
        CustomTabsIntent
            .Builder()
            .setDefaultColorSchemeParams(colors)
            .setShowTitle(true)
            .setUrlBarHidingEnabled(true)
            .build()

    try {
        customTab.launchUrl(this, uri)
    } catch (_: ActivityNotFoundException) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (_: ActivityNotFoundException) {
        }
    }
}
