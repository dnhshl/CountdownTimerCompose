package com.example.countdowntimer

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.concurrent.timer

class MainViewModel: ViewModel() {

    var timerValue by mutableStateOf(60)
    var currentTimerValue by mutableStateOf(timerValue)
    var ringAlarm by mutableStateOf(false)
    private var timerIsOn = false
    private var timerJob: Job? = null


    fun setNewTimerValue(newTimerValue: Int) {
        timerValue = newTimerValue
        currentTimerValue = newTimerValue
    }

    fun countDownTimer(): Flow<Int> = flow {
        val startingValue = currentTimerValue
        var currentValue = startingValue
        emit(startingValue)
        while (currentValue > 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }


    fun startTimer() {
        Log.i(">>>>>", "startTimer")
        if (timerIsOn) stopTimer()
        timerIsOn = true
        timerJob = viewModelScope.launch {
            countDownTimer().cancellable().collect {
                currentTimerValue = it
                if (currentTimerValue == 0) ringAlarm = true
            }
        }
        Log.i(">>>>>", "startTimer $timerIsOn $timerJob")
    }

    fun stopTimer(reset: Boolean = false) {
        timerIsOn = false
        timerJob?.cancel()
        if (reset) currentTimerValue = timerValue
        Log.i(">>>>>", "stopTimer $timerIsOn $timerJob")
    }

    fun resetTimer(reset: Boolean = false) {
        timerIsOn = false
        currentTimerValue = timerValue
    }

}