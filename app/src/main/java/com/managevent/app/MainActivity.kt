package com.managevent.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
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
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    // Sembunyikan BottomBar pada layar Auth / Onboarding / Form / Checkout / Payment
                    val showBottomBar = (currentRoute != Screen.Onboarding.route) &&
                            (currentRoute != Screen.Login.route) &&
                            (currentRoute != Screen.Register.route) &&
                            (currentRoute != Screen.AgendaForm.route) &&
                            (currentRoute != Screen.KatalogForm.route) &&
                            (currentRoute != Screen.UlasanForm.route) &&
                            (currentRoute != Screen.Checkout.route) &&
                            (currentRoute?.startsWith("payment/") != true)

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        bottomBar = {
                            if (showBottomBar) {
                                AppBottomBar(
                                    currentRoute = currentRoute,
                                    onNavigate = { route ->
                                        if (route == Screen.KatalogList.route) {
                                            val popped = navController.popBackStack(Screen.KatalogList.route, inclusive = false)
                                            if (!popped) {
                                                navController.navigate(Screen.KatalogList.route) {
                                                    popUpTo(0) { inclusive = true }
                                                }
                                            }
                                        } else {
                                            navController.navigate(route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        AppNavHost(
                            navController = navController,
                            startDestination = Screen.KatalogList.route,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}