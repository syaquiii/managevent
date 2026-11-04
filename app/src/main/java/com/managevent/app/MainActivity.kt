package com.managevent.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.managevent.app.core.navigation.AppNavHost
import com.managevent.app.core.navigation.Screen
import com.managevent.app.core.theme.ManageventTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ManageventTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomAppBar {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(onClick = { navController.navigate(Screen.Login.route) }) {
                                    Text("Login")
                                }
                                Button(onClick = { navController.navigate(Screen.KatalogList.route) }) {
                                    Text("Katalog")
                                }
                                Button(onClick = { navController.navigate(Screen.UlasanList.route) }) {
                                    Text("Ulasan")
                                }
                                Button(onClick = { navController.navigate(Screen.Wishlist.route) }) {
                                    Text("Wishlist")
                                }
                                Button(onClick = { navController.navigate(Screen.AgendaList.route) }) {
                                    Text("Agenda")
                                }
                                Button(onClick = { navController.navigate(Screen.Profile.route) }) {
                                    Text("Profile")
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
