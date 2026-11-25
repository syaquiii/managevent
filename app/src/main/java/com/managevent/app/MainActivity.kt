package com.managevent.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.edit
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.managevent.app.core.components.AppBottomBar
import com.managevent.app.core.navigation.AppNavHost
import com.managevent.app.core.navigation.Screen
import com.managevent.app.core.theme.ManageventTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ManageventTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val prefs = remember { getSharedPreferences("managevent_prefs", MODE_PRIVATE) }
                    val onboardingSeen = remember { prefs.getBoolean("onboarding_seen", false) }
                    val initialDestination = remember {
                        if (onboardingSeen) Screen.KatalogList.route else Screen.Onboarding.route
                    }

                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    val topLevelRoutes = remember {
                        setOf(
                            Screen.KatalogList.route,
                            Screen.Wishlist.route,
                            Screen.RiwayatPemesanan.route,
                            Screen.AgendaList.route,
                            Screen.Profile.route
                        )
                    }
                    val showBottomBar = currentRoute in topLevelRoutes

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        bottomBar = {
                            if (showBottomBar) {
                                AppBottomBar(
                                    currentRoute = currentRoute,
                                    onNavigate = { route ->
                                        navController.navigate(route) {
                                            popUpTo(Screen.KatalogList.route) {
                                                saveState = (route != Screen.KatalogList.route)
                                            }
                                            launchSingleTop = true
                                            restoreState = (route != Screen.KatalogList.route)
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        AppNavHost(
                            navController = navController,
                            startDestination = initialDestination,
                            onOnboardingCompleted = {
                                prefs.edit { putBoolean("onboarding_seen", true) }
                            },
                            modifier = Modifier
                                .padding(innerPadding)
                                .consumeWindowInsets(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
