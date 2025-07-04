package com.shrimpdevs.digitalassistant.screens.event

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth
import com.shrimpdevs.digitalassistant.R
import com.shrimpdevs.digitalassistant.dao.EventDao
import com.shrimpdevs.digitalassistant.dao.EventResult
import com.shrimpdevs.digitalassistant.models.Event
import com.shrimpdevs.digitalassistant.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    eventDao: EventDao,
    auth: FirebaseAuth,
    navigateToCreateEvent: () -> Unit,
    onEventClick: (Event) -> Unit,
    navigateToSettings: () -> Unit,
    navHostController: NavHostController
) {
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    val userId = auth.currentUser?.uid

    // Efecto para cargar eventos iniciales
    LaunchedEffect(Unit) {
        userId?.let { id ->
            try {
                withContext(Dispatchers.IO) {
                    when (val result = eventDao.getAllEventsForUser(id)) {
                        is EventResult.Success -> {
                            withContext(Dispatchers.Main) {
                                events = result.data
                            }
                        }
                        is EventResult.Error -> {
                            Log.e("EventScreen", "Error al obtener eventos", result.exception)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("EventScreen", "Error al cargar eventos", e)
            }
        }
    }

    // Efecto para observar cambios en tiempo real
    DisposableEffect(userId) {
        userId?.let { id ->
            eventDao.observeEventsForUser(id) { updatedEvents ->
                events = updatedEvents
            }
        }
        onDispose {
            eventDao.clearListeners()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Eventos", color = White) },
                actions = {
                    IconButton(onClick = navigateToSettings) {
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToCreateEvent,
                containerColor = DarkText,
                contentColor = White
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.create_icon),
                    contentDescription = "Crear evento"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(DarkBlue, Black)))
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(events) { event ->
                    EventCard(
                        event = event,
                        eventDao = eventDao,
                        auth = auth,
                        onEventClick = onEventClick
                    )
                }
            }
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    eventDao: EventDao,
    auth: FirebaseAuth,
    onEventClick: (Event) -> Unit
) {
    val userId = auth.currentUser?.uid
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onEventClick(event) },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    Text(
                        text = event.description,
                        color = DarkText.copy(alpha = 0.8f)
                    )
                    Text(
                        text = event.getFormattedDate(),
                        color = DarkText.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Ubicación: ${event.location}",
                        color = DarkText.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Alarma: ${if (event.alarm) "Activada" else "Desactivada"}",
                        color = DarkText.copy(alpha = 0.6f)
                    )
                }

                IconButton(
                    onClick = {
                        userId?.let { id ->
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    when (val result = eventDao.deleteEvent(event.title, id)) {
                                        is EventResult.Success -> {
                                            Log.d("EventScreen", "Evento eliminado con éxito")
                                        }
                                        is EventResult.Error -> {
                                            Log.e("EventScreen", "Error al eliminar evento", result.exception)
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("EventScreen", "Error al eliminar evento", e)
                                }
                            }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Eliminar",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}