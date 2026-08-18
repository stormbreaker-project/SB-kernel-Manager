package dev.danascape.kernelmanager.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prauga.pvot.designsystem.components.navigation.PvotNavBar
import com.prauga.pvot.designsystem.components.navigation.PvotTabItem
import dev.danascape.kernelmanager.navigation.SBDestination
import dev.danascape.kernelmanager.navigation.SBNavHost
import dev.danascape.kernelmanager.navigation.navigateToTopLevel
import dev.danascape.kernelmanager.navigation.topLevelDestination

/**
 * The app shell: one nav bar over the top-level destinations.
 */
@Composable
fun SBApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    // Before the first entry settles, the start destination is what is on
    // screen — reflect that rather than leaving the bar unselected.
    val current = backStackEntry?.destination.topLevelDestination() ?: SBDestination.NEWS

    val tabs = remember {
        SBDestination.entries.map {
            PvotTabItem(
                iconRes = it.iconRes,
                labelRes = it.labelRes,
                contentDescriptionRes = it.contentDescriptionRes,
                expandedIconRes = it.expandedIconRes,
                expandedLabelRes = it.expandedLabelRes,
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            PvotNavBar(
                selectedTab = current.ordinal,
                onTabClick = { index -> navController.navigateToTopLevel(SBDestination.entries[index]) },
                tabs = tabs,
            )
        },
    ) { innerPadding ->
        SBNavHost(
            navController = navController,
            contentPadding = innerPadding,
        )
    }
}
