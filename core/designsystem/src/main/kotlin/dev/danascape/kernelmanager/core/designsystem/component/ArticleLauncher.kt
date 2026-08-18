// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.designsystem.component

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

/**
 * Opens an article on the website in a Custom Tab.
 *
 * The post bodies are not in the feed, so the website renders them — which also
 * means the article looks exactly as it does on the web, with no second
 * Markdown pipeline to drift out of sync. The toolbar is painted with the app's
 * own surface colours so the handoff still reads as one product.
 *
 * Falls back to an ordinary view intent when no Custom Tabs provider exists,
 * and does nothing at all when the device has no browser, rather than crashing.
 */
fun Context.openArticle(url: String, toolbarColor: Int) {
    val uri = url.toUri()
    val colors = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(toolbarColor)
        .build()

    val customTab = CustomTabsIntent.Builder()
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
            // No browser on the device. Nothing sensible left to do.
        }
    }
}
