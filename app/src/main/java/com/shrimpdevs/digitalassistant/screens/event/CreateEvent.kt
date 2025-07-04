package com.shrimpdevs.digitalassistant.screens.event

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.shrimpdevs.digitalassistant.R
import com.shrimpdevs.digitalassistant.models.Event
import com.shrimpdevs.digitalassistant.ui.theme.BackgroundTextField
import com.shrimpdevs.digitalassistant.ui.theme.Black
import com.shrimpdevs.digitalassistant.ui.theme.DarkBlue
import com.shrimpdevs.digitalassistant.ui.theme.DarkText
import com.shrimpdevs.digitalassistant.ui.theme.SelectedField
import com.shrimpdevs.digitalassistant.ui.theme.White
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.shrimpdevs.digitalassistant.dao.EventDao
import com.shrimpdevs.digitalassistant.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shrimpdevs.digitalassistant.notifications.*
import com.shrimpdevs.digitalassistant.notifications.TaskReminderReceiver


import com.shrimpdevs.digitalassistant.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEvent(
    eventDao: EventDao,
    auth: FirebaseAuth,
    navigateToEvent: () -> Unit
) {

    val context= LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var alarm by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance(TimeZone.getDefault())) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    showTimePicker = true
                }) {
                    Text("Confirmar")
                }
            }
        ) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate.timeInMillis
            )
            DatePicker(state = datePickerState)
            LaunchedEffect(datePickerState.selectedDateMillis) {
                datePickerState.selectedDateMillis?.let { millis ->
                    selectedDate.timeInMillis = millis
                }
            }
        }
    }

    if (showTimePicker) {
        BasicAlertDialog(
            onDismissRequest = { showTimePicker = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val timePickerState = rememberTimePickerState(
                        initialHour = selectedDate.get(Calendar.HOUR_OF_DAY),
                        initialMinute = selectedDate.get(Calendar.MINUTE)
                    )
                    TimePicker(state = timePickerState)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Cancelar")
                        }
                        TextButton(
                            onClick = {
                                selectedDate.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                selectedDate.set(Calendar.MINUTE, timePickerState.minute)
                                showTimePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkBlue, Black)))
            .padding(horizontal = 35.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "Back Icon",
            tint = White,
            modifier = Modifier
                .padding(top = 20.dp)
                .clickable { navigateToEvent() }
                .align(Alignment.Start)
                .size(45.dp)
                .shadow(10.dp, shape = RoundedCornerShape(15.dp))
        )

        Text(
            text = "Crear Evento",
            color = White,
            fontSize = 24.sp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        TextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("Título") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = BackgroundTextField,
                focusedContainerColor = SelectedField,
            )
        )

        TextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text("Descripción") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = BackgroundTextField,
                focusedContainerColor = SelectedField,
            )
        )

        Button(
            onClick = { showDatePicker = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BackgroundTextField)
        ) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            Text("Fecha y Hora: ${dateFormat.format(selectedDate.time)}")
        }

        TextField(
            value = location,
            onValueChange = { location = it },
            placeholder = { Text("Ubicación") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = BackgroundTextField,
                focusedContainerColor = SelectedField,
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Alarma", color = White)
            Switch(
                checked = alarm,
                onCheckedChange = { alarm = it }
            )
        }
        //shonk.png :3
        var showImage by remember { mutableStateOf(false) }
        if (showImage) {
            AnimatedVisibility(
                visible = showImage,
                enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                exit = fadeOut(animationSpec = tween(durationMillis = 500))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.shonk),
                    contentDescription = "Evento creado",
                    modifier = Modifier
                        .padding(16.dp)
                        .size(200.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

        //boton para enviar
        Button(
            onClick = @androidx.annotation.RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS) {
                showImage=true

                val helper = NotificationHelper

                val event = Event(
                    title = title,
                    description = description,
                    eventDate = Timestamp(selectedDate.time),
                    location = location,
                    alarm = alarm
                )
                if (alarm) {
                    val eventTimeMillis = selectedDate.timeInMillis
                    val eventId = (0..Int.MAX_VALUE).random()

                    if (checkExactAlarmPermission(context)) {
                        scheduleEventNotification(
                            context = context,
                            eventId = eventId,
                            title = title,
                            description = "Tu evento es en 10 minutos: $title",
                            eventTimeMillis = eventTimeMillis- (10*60*1000)
                        )
                        scheduleEventNotification(
                            context = context,
                            eventId = eventId,
                            title = title,
                            description = "Tu evento es ahora!!: $title",
                            eventTimeMillis = eventTimeMillis
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Necesitas habilitar permisos de alarmas exactas en Configuración",
                            Toast.LENGTH_LONG
                        ).show()
                        requestExactAlarmPermission(context)
                    }
                }
                // Esta notificación es opcional (instantánea, no programada)
                helper.showNotification(context, "Evento Creado", "Tu evento fue agendado para ${selectedDate.time}")

                auth.currentUser?.uid?.let { userId ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            eventDao.insertEvent(event, userId)
                            withContext(Dispatchers.Main) {
                                navigateToEvent()
                            }
                        } catch (e: Exception) {
                            Log.e("CreateEvent", "Error al crear evento", e)
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .shadow(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkText)
        ) {
            Text("Guardar Evento")
        }
    }
}
private fun createEvent(db: FirebaseFirestore, event: Event, onSuccess: () -> Unit) {
    db.collection("events")
        .add(event)
        .addOnSuccessListener {
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                onSuccess()
            }, 500)
        }
        .addOnFailureListener {
            Log.e("CreateEvent", "Error al crear evento", it)
        }
}