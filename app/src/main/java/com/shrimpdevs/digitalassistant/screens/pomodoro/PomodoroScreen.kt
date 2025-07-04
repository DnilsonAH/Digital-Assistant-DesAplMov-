package com.shrimpdevs.digitalassistant.pomodoro

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.ui.graphics.Brush


// Colores
val MaxBlue = Color(0xFF1E88E5)
val LightGray = Color(0xFFEEEEEE)
val StartGradientColor = Color(0xFF1B1464)
val EndGradientColor = Color(0xFF000000)

@Composable
fun PomodoroScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val timerManager = remember { PomodoroTimerManager() }

    val timeSelected by timerManager.timeSelected.collectAsState()
    val timeProgress by timerManager.timeProgress.collectAsState()
    val isTimerRunning by timerManager.isTimerRunning.collectAsState()
    val displayTime by timerManager.displayTime.collectAsState() // Correcto: displayTime

    var showTimePickerDialog by remember { mutableStateOf(false) }

    if (showTimePickerDialog) {
        TimePickerDialog(
            onDismissRequest = { showTimePickerDialog = false },
            onSetTime = { hours, minutes, seconds ->
                timerManager.setTime(hours, minutes, seconds)
                showTimePickerDialog = false
            },
            initialHours = timeSelected / 3600,
            initialMinutes = (timeSelected % 3600) / 60,
            initialSeconds = timeSelected % 60
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(StartGradientColor, EndGradientColor),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .padding(16.dp), // Mantén el padding
        contentAlignment = Alignment.Center
    ) {
        // --- Botón de Retroceso ---
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(250.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = LightGray.copy(alpha = 0.3f),
                        radius = size.minDimension / 2,
                        style = Stroke(width = 12.dp.toPx())
                    )
                }

                if (timeSelected > 0) {
                    val sweepAngle = (timeProgress.toFloat() / timeSelected.toFloat()) * 360f
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = MaxBlue,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = displayTime,
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaxBlue
                    )
                    Text(
                        text = "Tiempo restante",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImageButton(
                    onClick = { showTimePickerDialog = true },
                    icon = Icons.Default.Add,
                    description = "Añadir tiempo",
                    backgroundColor = Color.Transparent,
                    iconTint = Color.White,
                    modifier = Modifier.size(48.dp)
                )

                Button(
                    onClick = {
                        if (isTimerRunning) {
                            timerManager.pauseTimer()
                        } else {
                            timerManager.startTimer()
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaxBlue),
                    modifier = Modifier
                        .height(50.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isTimerRunning) "Pausar" else "Empezar",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isTimerRunning) "Pausar" else "Empezar",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }

                ImageButton(
                    onClick = { timerManager.resetTimer() },
                    icon = Icons.Default.Refresh,
                    description = "Reiniciar",
                    backgroundColor = Color.Transparent,
                    iconTint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (timeSelected != 0) {
                        timerManager.addExtraTime(15)
                        Toast.makeText(context, "15 segundos añadidos", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Establece el tiempo primero", Toast.LENGTH_SHORT).show()
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaxBlue.copy(alpha = 0.7f)),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(45.dp)
            ) {
                Text(text = "+15s", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onSetTime: (hours: Int, minutes: Int, seconds: Int) -> Unit,
    initialHours: Int,
    initialMinutes: Int,
    initialSeconds: Int
) {
    var hours by remember { mutableStateOf(initialHours) }
    var minutes by remember { mutableStateOf(initialMinutes) }
    var seconds by remember { mutableStateOf(initialSeconds) }
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimePickerColumn(
                        range = 0..23,
                        selectedValue = hours,
                        onValueChange = { hours = it },
                        label = "h"
                    )
                    TimePickerColumn(
                        range = 0..59,
                        selectedValue = minutes,
                        onValueChange = { minutes = it },
                        label = "m"
                    )
                    TimePickerColumn(
                        range = 0..59,
                        selectedValue = seconds,
                        onValueChange = { seconds = it },
                        label = "s"
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val newTimeSelected = hours * 3600 + minutes * 60 + seconds
                        if (newTimeSelected == 0) {
                            Toast.makeText(context, "Por favor, introduce una duración válida", Toast.LENGTH_SHORT).show()
                        } else {
                            onSetTime(hours, minutes, seconds)
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaxBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("OK", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TimePickerColumn(
    range: IntRange,
    selectedValue: Int,
    onValueChange: (Int) -> Unit,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = {
                val newValue = if (selectedValue + 1 > range.last) range.first else selectedValue + 1
                onValueChange(newValue)
            },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Aumentar $label", tint = Color.Black)
        }

        BasicTextField(
            value = String.format("%02d", selectedValue),
            onValueChange = { newValue ->
                val intValue = newValue.toIntOrNull()
                if (intValue != null && intValue in range) {
                    onValueChange(intValue)
                } else if (newValue.isEmpty()) {
                    onValueChange(0)
                }
            },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .width(60.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
        )
        Text(text = label, fontSize = 16.sp, color = Color.Gray)

        IconButton(
            onClick = {
                val newValue = if (selectedValue - 1 < range.first) range.last else selectedValue - 1
                onValueChange(newValue)
            },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Disminuir $label", tint = Color.Black)
        }
    }
}

@Composable
fun ImageButton(
    onClick: () -> Unit,
    icon: ImageVector,
    description: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    iconTint: Color = MaterialTheme.colorScheme.onPrimary,
    buttonSize: Dp = 56.dp
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(buttonSize),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = iconTint,
            modifier = Modifier.size(buttonSize * 0.6f)
        )
    }
}