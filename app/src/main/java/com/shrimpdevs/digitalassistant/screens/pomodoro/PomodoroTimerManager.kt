package com.shrimpdevs.digitalassistant.pomodoro

import android.content.Context
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.shrimpdevs.digitalassistant.R

class PomodoroTimerManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    private var _showAlarmDialog = MutableStateFlow(false)
    val showAlarmDialog: StateFlow<Boolean> = _showAlarmDialog.asStateFlow()

    private var timeCountDown: CountDownTimer? = null
    private var _timeSelected = MutableStateFlow(0)
    val timeSelected: StateFlow<Int> = _timeSelected.asStateFlow()

    private var _timeProgress = MutableStateFlow(0)
    val timeProgress: StateFlow<Int> = _timeProgress.asStateFlow()

    private var _pauseOffSet = MutableStateFlow(0L)
    val pauseOffSet: StateFlow<Long> = _pauseOffSet.asStateFlow()

    private var _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private var _displayTime = MutableStateFlow("00:00:00")
    val displayTime: StateFlow<String> = _displayTime.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound)
            mediaPlayer?.isLooping = true
        } catch (e: Exception) {
            Log.e("PomodoroTimerManager", "Error al inicializar MediaPlayer", e)
        }
    }

    fun setTime(hours: Int, minutes: Int, seconds: Int) {
        val totalSeconds = hours * 3600 + minutes * 60 + seconds
        _timeSelected.value = totalSeconds
        _timeProgress.value = totalSeconds
        updateDisplayTime(totalSeconds)
    }

    fun startTimer() {
        if (_timeSelected.value <= 0) return

        _isTimerRunning.value = true
        timeCountDown = object : CountDownTimer(
            (_timeProgress.value * 1000).toLong(),
            1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                _timeProgress.value = (millisUntilFinished / 1000).toInt()
                updateDisplayTime(_timeProgress.value)
            }

            override fun onFinish() {
                stopTimer()
            }
        }.start()
    }

    fun pauseTimer() {
        timeCountDown?.cancel()
        _isTimerRunning.value = false
    }

    fun resetTimer() {
        stopTimer()
        stopAlarm()
        _timeProgress.value = _timeSelected.value
        _isTimerRunning.value = false
        updateDisplayTime(_timeSelected.value)
    }

    fun dismissAlarm() {
        _showAlarmDialog.value = false
        stopAlarm()
    }

    fun addExtraTime(seconds: Int) {
        val newProgress = _timeProgress.value + seconds
        _timeProgress.value = newProgress
        if (_isTimerRunning.value) {
            pauseTimer()
            startTimer()
        } else {
            updateDisplayTime(newProgress)
        }
    }

    private fun playAlarm() {
        try {
            mediaPlayer?.start()
        } catch (e: Exception) {
            Log.e("PomodoroTimerManager", "Error al reproducir alarma", e)
        }
    }

    private fun stopAlarm() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                    prepare()
                }
            }
        } catch (e: Exception) {
            Log.e("PomodoroTimerManager", "Error al detener alarma", e)
        }
    }

    private fun stopTimer() {
        timeCountDown?.cancel()
        timeCountDown = null
        _isTimerRunning.value = false
        if (_timeProgress.value <= 0) {
            _showAlarmDialog.value = true
            playAlarm()
        }
    }

    private fun updateDisplayTime(seconds: Int) {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        _displayTime.value = String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    fun clear() {
        stopTimer()
        stopAlarm()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}