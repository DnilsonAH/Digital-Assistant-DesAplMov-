package com.shrimpdevs.digitalassistant.screens.pomodoro

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageButton
import android.widget.NumberPicker // ¡Importamos NumberPicker!
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.shrimpdevs.digitalassistant.R

class PomoMain : AppCompatActivity() {

    private var timeSelected: Int = 0 // Tiempo total seleccionado en segundos
    private var timeCountDown: CountDownTimer? = null
    private var timeProgress = 0 // Progreso actual del contador en segundos (desde el inicio del ciclo actual)
    private var pauseOffSet: Long = 0 // Millis que han transcurrido en el timer actual antes de la pausa
    private var isStart = true // Indica si el botón de inicio debe iniciar o pausar/reanudar

    // Referencias a vistas principales (para evitar findViewById repetitivos)
    private lateinit var progressBar: ProgressBar
    private lateinit var timeLeftTv: TextView
    private lateinit var startBtn: Button
    private lateinit var addBtn: ImageButton
    private lateinit var resetBtn: ImageButton
    private lateinit var addTimeTv: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pomodoro)

        // Inicializar las vistas una sola vez
        progressBar = findViewById(R.id.pbTimer)
        timeLeftTv = findViewById(R.id.tvTimeLeft)
        startBtn = findViewById(R.id.btnPlayPause)
        addBtn = findViewById(R.id.btnAdd)
        resetBtn = findViewById(R.id.ib_reset)
        addTimeTv = findViewById(R.id.tv_addTime)


        addBtn.setOnClickListener {
            setTimeFunction()
        }
        startBtn.setOnClickListener {
            startTimerSetup()
        }
        resetBtn.setOnClickListener {
            resetTime()
        }
        addTimeTv.setOnClickListener {
            addExtraTime()
        }

        // Asegurarse de que el tiempo inicial sea 00:00:00 al iniciar
        updateTimerDisplay(0)
    }

    private fun addExtraTime() {
        if (timeSelected != 0) {
            val oldTimeSelected = timeSelected // Guardamos el tiempo total anterior
            timeSelected += 15 // Añade 15 segundos al tiempo total
            progressBar.max = timeSelected // Actualiza el máximo de la barra de progreso

            timePause() // Pausa el temporizador actual si está corriendo

            // Ajustar pauseOffSet para que el tiempo restante sea correcto
            // Si el timer estaba en 100 segundos y pasaron 10 (timeProgress=10), quedan 90.
            // Si le añado 15, el nuevo total es 115. Queremos que queden 90 + 15 = 105.
            // pauseOffSet = (oldTimeSelected - (oldTimeSelected - timeProgress)).toLong()
            // Una forma más simple es recalcular el tiempo transcurrido con respecto al nuevo total
            // timeProgress es el tiempo ya transcurrido desde el inicio del set actual
            // El tiempo que ya ha corrido desde el inicio (en milisegundos)
            val elapsedMillis = pauseOffSet * 1000L // pauseOffSet ya contiene los segundos transcurridos
            val remainingMillisBeforeAdd = (oldTimeSelected.toLong() * 1000L) - elapsedMillis
            val newTotalMillis = timeSelected.toLong() * 1000L
            val newRemainingMillis = remainingMillisBeforeAdd + (15 * 1000L)

            // El nuevo pauseOffSet será la diferencia entre el nuevo total y el nuevo restante
            pauseOffSet = (newTotalMillis - newRemainingMillis) / 1000L

            startTimer(pauseOffSet) // Reinicia el timer con el nuevo offset (tiempo transcurrido)

            Toast.makeText(this, "15 segundos añadidos", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Por favor, establece un tiempo primero.", Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun resetTime() {
        if (timeCountDown != null) {
            timeCountDown!!.cancel()
        }
        timeProgress = 0
        timeSelected = 0
        pauseOffSet = 0
        timeCountDown = null
        startBtn.text = "Empezar"
        isStart = true
        progressBar.progress = 0
        updateTimerDisplay(0) // Actualiza el display a 00:00:00
    }

    private fun timePause() {
        if (timeCountDown != null) {
            timeCountDown!!.cancel()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun startTimerSetup() {
        // Asegúrate de que timeSelected es el tiempo total establecido por el usuario
        // timeProgress es el tiempo transcurrido desde el inicio del CountDownTimer
        val timeLeft = timeSelected - pauseOffSet.toInt() // Tiempo restante en segundos

        if (timeSelected > 0) { // Si hay tiempo total establecido
            if (isStart) {
                startBtn.text = "Pausar"
                startTimer(pauseOffSet) // Pasa el tiempo transcurrido hasta el momento
                isStart = false
            } else {
                isStart = true
                startBtn.text = "Reanudar"
                timePause()
            }
        } else {
            Toast.makeText(this, "Por favor, establece un tiempo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer(offsetSeconds: Long) {
        // El CountDownTimer necesita el tiempo TOTAL RESTANTE EN MILISEGUNDOS para contar
        // timeSelected es el tiempo total original.
        // offsetSeconds es el tiempo ya transcurrido desde el inicio del temporizador.
        val remainingMillis = (timeSelected.toLong() * 1000) - (offsetSeconds * 1000)

        timeCountDown = object : CountDownTimer(remainingMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Calcular el tiempo transcurrido desde el inicio del temporizador completo
                pauseOffSet = (timeSelected.toLong() * 1000 - millisUntilFinished) / 1000

                // timeProgress debería reflejar el tiempo transcurrido desde el inicio del ciclo
                // de la barra de progreso. La barra de progreso avanza de 0 a timeSelected.
                timeProgress = pauseOffSet.toInt() // Asumiendo que timeProgress es para el avance de la barra

                progressBar.progress = timeProgress
                progressBar.max = timeSelected // Asegúrate que el máximo siempre sea el tiempo seleccionado

                val timeLeftInSeconds = millisUntilFinished / 1000 // Segundos restantes
                updateTimerDisplay(timeLeftInSeconds)
            }

            override fun onFinish() {
                resetTime()
                Toast.makeText(this@PomoMain, "¡Tiempo terminado!", Toast.LENGTH_SHORT).show()
            }

        }.start()
    }

    @SuppressLint("SetTextI18n")
    private fun updateTimerDisplay(seconds: Long) {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        timeLeftTv.text = String.format("%02d:%02d:%02d", hours, minutes, secs)
    }


    @SuppressLint("SetTextI18n")
    private fun setTimeFunction() {
        val timeDialog = Dialog(this)
        timeDialog.setContentView(R.layout.add_dialog)

        // ****** Referencias a los nuevos NumberPickers ******
        val npHours = timeDialog.findViewById<NumberPicker>(R.id.np_hours)
        val npMinutes = timeDialog.findViewById<NumberPicker>(R.id.np_minutes)
        val npSeconds = timeDialog.findViewById<NumberPicker>(R.id.np_seconds)
        // **************************************************

        val btnOk = timeDialog.findViewById<Button>(R.id.btnOk)

        // ****** Configurar los NumberPickers ******
        npHours.minValue = 0
        npHours.maxValue = 23 // Máximo 23 horas (puedes ajustar si necesitas más)
        npHours.value = 0 // Valor inicial

        npMinutes.minValue = 0
        npMinutes.maxValue = 59
        npMinutes.value = 0

        npSeconds.minValue = 0
        npSeconds.maxValue = 59
        npSeconds.value = 0
        // *****************************************

        // Opcional: Establecer los valores iniciales de los NumberPickers
        // con el tiempo actual si se está editando un tiempo ya establecido
        if (timeSelected > 0) {
            val currentHours = timeSelected / 3600
            val currentMinutes = (timeSelected % 3600) / 60
            val currentSeconds = timeSelected % 60
            npHours.value = currentHours
            npMinutes.value = currentMinutes
            npSeconds.value = currentSeconds
        }


        btnOk.setOnClickListener {
            // ****** Obtener los valores de los NumberPickers ******
            val hours = npHours.value
            val minutes = npMinutes.value
            val seconds = npSeconds.value
            // ****************************************************

            // Calcular el tiempo total en segundos
            val newTimeSelected = hours * 3600 + minutes * 60 + seconds

            if (newTimeSelected == 0) { // Si el tiempo total es 0
                Toast.makeText(this, "Por favor, introduce una duración válida", Toast.LENGTH_SHORT).show()
            } else {
                resetTime() // Reinicia el temporizador actual
                timeSelected = newTimeSelected // Asigna el nuevo tiempo total
                progressBar.max = timeSelected // El máximo de la progress bar es el tiempo total en segundos
                progressBar.progress = 0 // Inicia el progreso en 0

                // Mostrar el tiempo inicial formateado
                updateTimerDisplay(timeSelected.toLong())

                startBtn.text = "Empezar"
            }
            timeDialog.dismiss()
        }

        // ****** Código para hacer el fondo del diálogo transparente ******
        timeDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // ****************************************************************

        timeDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (timeCountDown != null) {
            timeCountDown?.cancel()
            timeProgress = 0
        }
    }
}