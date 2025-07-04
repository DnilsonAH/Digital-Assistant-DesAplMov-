package com.shrimpdevs.digitalassistant.pomodoro

import android.os.CountDownTimer
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class PomodoroTimerManager {
    private var timeCountDown: CountDownTimer? = null
    private var _timeSelected = MutableStateFlow(0) // Tiempo total seleccionado en segundos
    val timeSelected: StateFlow<Int> = _timeSelected.asStateFlow()

    private var _timeProgress = MutableStateFlow(0) // Progreso actual del contador en segundos
    val timeProgress: StateFlow<Int> = _timeProgress.asStateFlow()

    private var _pauseOffSet = MutableStateFlow(0L) // Segundos que han transcurrido antes de la pausa
    val pauseOffSet: StateFlow<Long> = _pauseOffSet.asStateFlow()

    private var _isTimerRunning = MutableStateFlow(false) // Indica si el temporizador está corriendo
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private var _displayTime = MutableStateFlow("00:00:00") // Tiempo formateado para mostrar
    val displayTime: StateFlow<String> = _displayTime.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)


    fun setTime(hours: Int, minutes: Int, seconds: Int) {
        stopTimer() // Detiene cualquier temporizador en curso
        _timeSelected.value = hours * 3600 + minutes * 60 + seconds
        _timeProgress.value = 0
        _pauseOffSet.value = 0L
        _isTimerRunning.value = false
        updateDisplayTime(_timeSelected.value.toLong())
    }

    fun startTimer() {
        if (_timeSelected.value == 0) {
            return
        }

        val remainingMillis = (_timeSelected.value.toLong() * 1000) - (_pauseOffSet.value * 1000)

        if (remainingMillis <= 0 && !_isTimerRunning.value) { // Si ya terminó o no hay tiempo, reinicia
            updateDisplayTime(0)
            return
        }

        _isTimerRunning.value = true
        timeCountDown = object : CountDownTimer(remainingMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _pauseOffSet.value = (_timeSelected.value.toLong() * 1000 - millisUntilFinished) / 1000
                _timeProgress.value = _pauseOffSet.value.toInt() // Progreso de la barra
                updateDisplayTime(millisUntilFinished / 1000)
            }

            override fun onFinish() {
                stopTimer()
                updateDisplayTime(0)
            }
        }.start()
    }

    fun pauseTimer() {
        timeCountDown?.cancel()
        _isTimerRunning.value = false
    }

    fun resetTimer() {
        stopTimer()
        _timeSelected.value = 0
        _timeProgress.value = 0
        _pauseOffSet.value = 0L
        _isTimerRunning.value = false
        updateDisplayTime(0)
    }

    fun addExtraTime(secondsToAdd: Int) {
        if (_timeSelected.value != 0) {
            val oldTimeSelected = _timeSelected.value
            _timeSelected.value += secondsToAdd // Añade tiempo al total

            val elapsedMillis = _pauseOffSet.value * 1000L
            val remainingMillisBeforeAdd = (oldTimeSelected.toLong() * 1000L) - elapsedMillis
            val newTotalMillis = _timeSelected.value.toLong() * 1000L
            val newRemainingMillis = remainingMillisBeforeAdd + (secondsToAdd * 1000L)

            _pauseOffSet.value = (newTotalMillis - newRemainingMillis) / 1000L

            if (_isTimerRunning.value) {
                stopTimer()
                startTimer()
            } else {

                updateDisplayTime((_timeSelected.value.toLong() - _pauseOffSet.value))
            }
        }
    }

    private fun stopTimer() {
        timeCountDown?.cancel()
        timeCountDown = null
    }

    private fun updateDisplayTime(seconds: Long) {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        _displayTime.value = String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    fun clear() {
        stopTimer()
    }
}