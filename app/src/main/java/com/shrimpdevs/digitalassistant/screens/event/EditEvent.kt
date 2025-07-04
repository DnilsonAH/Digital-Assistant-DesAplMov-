package com.shrimpdevs.digitalassistant.screens.event

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shrimpdevs.digitalassistant.R
import com.shrimpdevs.digitalassistant.dao.EventDao
import com.shrimpdevs.digitalassistant.models.Event
import com.shrimpdevs.digitalassistant.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEvent(
    eventDao: EventDao,
    auth: FirebaseAuth,
    event: Event,
    navigateBack: () -> Unit
) {
    var title by remember { mutableStateOf(event.title) }
    var description by remember { mutableStateOf(event.description) }
    var location by remember { mutableStateOf(event.location) }
    var alarm by remember { mutableStateOf(event.alarm) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance().apply {
        time = event.eventDate.toDate()
    }) }

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
                .clickable { navigateBack() }
                .align(Alignment.Start)
                .size(45.dp)
                .shadow(10.dp, shape = RoundedCornerShape(15.dp))
        )

        Text(
            text = "Editar Evento",
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

        Button(
            onClick = {
                val updatedEvent = Event(
                    title = title,
                    description = description,
                    eventDate = Timestamp(selectedDate.time),
                    location = location,
                    alarm = alarm
                )
                auth.currentUser?.uid?.let { userId ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            eventDao.updateEvent(event.title, updatedEvent, userId)
                            withContext(Dispatchers.Main) {
                                navigateBack()
                            }
                        } catch (e: Exception) {
                            Log.e("EditEvent", "Error al actualizar evento", e)
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
            Text("Guardar Cambios")
        }
    }
}

private fun updateEvent(
    db: FirebaseFirestore,
    originalTitle: String,
    updatedEvent: Event,
    onSuccess: () -> Unit
) {
    db.collection("events")
        .whereEqualTo("title", originalTitle)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                document.reference.set(updatedEvent)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Log.e("EditEvent", "Error al actualizar evento", e)
                    }
            }
        }
}