package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ServerListScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.screens.VpsGuideScreen
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.SecureV2RayTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SecureV2RayTheme {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(viewModel: MainViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val isAdminMode by viewModel.isAdminMode.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = CyberCardBg,
                contentColor = CyberCyanPrimary,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyberCyanPrimary,
                        indicatorColor = CyberCyanPrimary,
                        unselectedIconColor = CyberTextSecondary,
                        unselectedTextColor = CyberTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_home")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Dns, contentDescription = "Servers") },
                    label = { Text("Servers", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyberCyanPrimary,
                        indicatorColor = CyberCyanPrimary,
                        unselectedIconColor = CyberTextSecondary,
                        unselectedTextColor = CyberTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_servers")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Terminal, contentDescription = "VPS Guide") },
                    label = { Text("VPS Guide", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyberCyanPrimary,
                        indicatorColor = CyberCyanPrimary,
                        unselectedIconColor = CyberTextSecondary,
                        unselectedTextColor = CyberTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_vps")
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyberCyanPrimary,
                        indicatorColor = CyberCyanPrimary,
                        unselectedIconColor = CyberTextSecondary,
                        unselectedTextColor = CyberTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_profile")
                )

                if (isAdminMode) {
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                        label = { Text("Admin", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = CyberAmber,
                            indicatorColor = CyberAmber,
                            unselectedIconColor = CyberTextSecondary,
                            unselectedTextColor = CyberTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_admin")
                    )
                }
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToServers = { selectedTab = 1 }
                )
                1 -> ServerListScreen(
                    viewModel = viewModel,
                    onServerSelected = { server ->
                        viewModel.selectServer(server)
                        selectedTab = 0
                    }
                )
                2 -> VpsGuideScreen()
                3 -> ProfileScreen(viewModel = viewModel)
                4 -> AdminPanelScreen(viewModel = viewModel)
            }
        }
    }
}
