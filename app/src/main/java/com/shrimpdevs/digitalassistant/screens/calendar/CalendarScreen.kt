package com.shrimpdevs.digitalassistant.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.shrimpdevs.digitalassistant.R
import com.shrimpdevs.digitalassistant.ui.theme.Black
import com.shrimpdevs.digitalassistant.ui.theme.DarkBlue
import com.shrimpdevs.digitalassistant.ui.theme.DarkText
import com.shrimpdevs.digitalassistant.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navHostController: NavHostController
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Calendario", color = White) },
                navigationIcon = {
                    IconButton(onClick = { navHostController.navigate("event") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Regresar",
                            tint = White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navHostController.navigate("Settings") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "Ajustes",
                            tint = White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkBlue
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkBlue,
                contentColor = White
            ) {
                val navBackStackEntry by navHostController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val navItems = listOf(
                    Triple("Inicio", R.drawable.ic_event, "event"),
                    Triple("Calendario", R.drawable.ic_calendar, "calendar_route"),
                    Triple("Pomodoro", R.drawable.ic_access_time, "pomodoro_screen_route")
                )

                navItems.forEach { (label, iconResId, route) ->
                    NavigationBarItem(
                        selected = currentRoute == route,
                        onClick = {
                            if (currentRoute != route) {
                                navHostController.navigate(route)
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = iconResId),
                                contentDescription = label
                            )
                        },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = White,
                            unselectedIconColor = White.copy(alpha = 0.5f),
                            selectedTextColor = White,
                            unselectedTextColor = White.copy(alpha = 0.5f),
                            indicatorColor = DarkText
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(DarkBlue, Black)))
                .padding(paddingValues)
        ) {
            // Tu contenido del calendario aquí
        }
    }
}