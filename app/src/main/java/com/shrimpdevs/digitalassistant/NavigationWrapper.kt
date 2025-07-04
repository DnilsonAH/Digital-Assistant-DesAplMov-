package com.shrimpdevs.digitalassistant

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shrimpdevs.digitalassistant.animaciones.AnimatedNavigation // Asegúrate de que este import sea correcto
import com.shrimpdevs.digitalassistant.models.Event
import com.shrimpdevs.digitalassistant.screens.calendar.CalendarScreen
import com.shrimpdevs.digitalassistant.screens.event.*
import com.shrimpdevs.digitalassistant.screens.pomodoro.PomodoroScreen
import com.shrimpdevs.digitalassistant.screens.presentation.*
import com.shrimpdevs.digitalassistant.screens.settings.SettingsScreen
import com.shrimpdevs.digitalassistant.service.FirebaseEventDao

@OptIn(ExperimentalAnimationApi::class) // Necesario por las transiciones de NavHost
@Composable
fun NavigationWrapper(
    navHostController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore
) {
    val context = LocalContext.current
    val eventDao = remember { FirebaseEventDao(db) }

    LaunchedEffect(auth.currentUser) {
        if (auth.currentUser != null) {
            if (navHostController.currentDestination?.route != "event" &&
                navHostController.currentDestination?.route != "pomodoro_screen_route" &&
                navHostController.currentDestination?.route != "calendar_route"
            ) {
                navHostController.navigate("event") {
                    popUpTo("initial") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(
        navController = navHostController,
        startDestination = "initial"
    ) {
        with(AnimatedNavigation) {
            composableWithFadeAnimation("initial") {
                InitialScreen(
                    navigateToLogin = { navHostController.navigate("logIn") },
                    navigateToSignUp = { navHostController.navigate("signUp") },
                    navigateToEvent = {
                        navHostController.navigate("event") {
                            popUpTo("initial") { inclusive = true }
                        }
                    },
                    auth = auth,
                    context = context
                )
            }

            composableWithFadeAnimation("logIn") {
                LoginScreen(
                    navigateToInitial = { navHostController.navigate("initial") },
                    navigateToEvent = { navHostController.navigate("event") },
                    auth = auth
                )
            }

            composableWithFadeAnimation("signUp") {
                SignUpScreen(
                    navigateToInitial = { navHostController.navigate("initial") },
                    navigateToEvent = { navHostController.navigate("event") },
                    auth = auth
                )
            }

            composableWithFadeAnimation("event") {
                EventScreen(
                    eventDao = eventDao,
                    auth = auth,
                    navigateToCreateEvent = { navHostController.navigate("CreateEvent") },
                    navigateToSettings = { navHostController.navigate("Settings") },
                    onEventClick = { event: Event ->
                        navHostController.currentBackStackEntry?.savedStateHandle?.set(
                            key = "event",
                            value = event
                        )
                        navHostController.navigate("EditEvent")
                    },
                    navHostController = navHostController
                )
            }

            composableWithFadeAnimation("Settings") {
                SettingsScreen(
                    navigateBack = { navHostController.navigate("event") },
                    navHostController = navHostController
                )
            }

            composableWithFadeAnimation("CreateEvent") {
                CreateEvent(
                    eventDao = eventDao,
                    auth = auth,
                    navigateToEvent = { navHostController.navigate("event") }
                )
            }

            composableWithFadeAnimation("EditEvent") {
                val event = navHostController.previousBackStackEntry?.savedStateHandle?.get<Event>("event")
                event?.let {
                    EditEvent(
                        eventDao = eventDao,
                        auth = auth,
                        event = it,
                        navigateBack = { navHostController.navigate("event") }
                    )
                }
            }

            composableWithFadeAnimation("pomodoro_screen_route") {
                PomodoroScreen(navHostController = navHostController)
            }

            composableWithFadeAnimation("calendar_route") {
                CalendarScreen(navHostController = navHostController)
            }
        }
    }
}