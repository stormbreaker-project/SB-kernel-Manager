// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.news

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object NewsRoute

fun NavController.navigateToNews(navOptions: NavOptions? = null) = navigate(NewsRoute, navOptions)

fun NavGraphBuilder.newsScreen(contentPadding: PaddingValues) {
    composable<NewsRoute> { NewsScreen(contentPadding = contentPadding) }
}
