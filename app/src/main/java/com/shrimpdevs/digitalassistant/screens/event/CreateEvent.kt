package com.shrimpdevs.digitalassistant.screens.event

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.shrimpdevs.digitalassistant.R
import com.shrimpdevs.digitalassistant.models.Event
import com.shrimpdevs.digitalassistant.ui.theme.Black
import com.shrimpdevs.digitalassistant.ui.theme.DarkBlue
import com.shrimpdevs.digitalassistant.ui.theme.DarkText
import com.shrimpdevs.digitalassistant.ui.theme.White
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import com.shrimpdevs.digitalassistant.ui.theme.BackgroundTextField
import com.shrimpdevs.digitalassistant.ui.theme.SelectedField


// Assuming these are defined in your project
// import com.yourpackage.R // For R.drawable.ic_back
// import com.yourpackage.ui.theme.BackgroundTextField
// import com.yourpackage.ui.theme.Black
// import com.yourpackage.ui.theme.DarkBlue
// import com.yourpackage.ui.theme.DarkText
// import com.yourpackage.ui.theme.SelectedField
// import com.yourpackage.ui.theme.White
// import com.yourpackage.model.Event // For your Event data class

@Composable
fun CreateEvent(db: FirebaseFirestore, navigateToEvent: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var alarm by remember { mutableStateOf(false) }

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
                .padding(top = 35.dp)
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
                val event = Event(
                    title = title,
                    description = description,
                    eventDate = Timestamp.now(),
                    location = location,
                    alarm = alarm
                )
                createEvent(db, event, navigateToEvent)
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

fun createEvent(db: FirebaseFirestore, event: Event, onSuccess: () -> Unit) {
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
