package com.example.countdowntimer.ui

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chargemap.compose.numberpicker.ListItemPicker
import com.example.countdowntimer.MainViewModel
import com.example.countdowntimer.R
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@Composable
fun MainScreen() {
    val vm: MainViewModel = viewModel()
    var showDialog by remember { mutableStateOf(false)}

    if (vm.ringAlarm)  {
        PlayAlarm()
        vm.ringAlarm = false
    }


    Box(modifier = Modifier.fillMaxSize()) {
        if (showDialog) {
            SetTimerDialog(
                setShowDialog = {showDialog = it},
                setValue = { dialogResult -> vm.setNewTimerValue(dialogResult) }
            )
        }


        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {

            TimerDisplay(vm.currentTimerValue)

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()

            ) {
                Button(onClick = { vm.resetTimer() }) {
                    Icon(painter = painterResource(id = R.drawable.ic_baseline_first_page_24), "Reset")
                }
                Button(onClick = { vm.startTimer() }) {
                    Icon(Icons.Filled.PlayArrow, "Start Timer")
                }
                Button(onClick = { vm.stopTimer() }) {
                    Icon(painter = painterResource(id = R.drawable.ic_baseline_pause_24), "Pause")
                }
                Button(onClick = { vm.stopTimer(reset = true) }) {
                    Icon(painter = painterResource(id = R.drawable.ic_baseline_stop_24), "Stop")
                }
                Button(onClick = { showDialog = true }) {
                    Icon(Icons.Filled.Add, "")
                }

            }
        }
    }
}



@OptIn(ExperimentalTime::class)
@Composable
fun TimerDisplay(
    timeInSeconds: Int = 0,
    modifier: Modifier = Modifier
) {
    val duration: Duration = timeInSeconds.seconds
    duration.toComponents { hours, minutes, seconds, nanoseconds ->
        Text(
            "%02d:%02d:%02d".format(hours, minutes, seconds),
            style = MaterialTheme.typography.h3
        )
    }

}



@OptIn(ExperimentalTime::class)
@Composable
fun TimerPicker(
    value: Int = 0,
    modifier: Modifier = Modifier,
    setTime: (Int, Int, Int) -> Unit
) {
    val hourValues = (0..24).toList()
    val minuteValues = (0..59).toList()
    val secondValues = (0..59).toList()
    var hours by remember { mutableStateOf(hourValues[0]) }
    var minutes by remember { mutableStateOf(minuteValues[0]) }
    var seconds by remember { mutableStateOf(secondValues[0]) }


    Log.i(">>>>>", "Time Picker $hours $minutes $seconds")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically

    ) {
        ListItemPicker(
            value = hours,
            onValueChange = {
                hours = it
                setTime(hours, minutes, seconds)
            },
            list = hourValues)
        Text(
            modifier = Modifier.size(24.dp),
            textAlign = TextAlign.Center,
            text = ":"
        )
        ListItemPicker(
            value = minutes,
            onValueChange = {
                minutes = it
                setTime(hours, minutes, seconds)
            },
            list = minuteValues)
        Text(
            modifier = Modifier.size(24.dp),
            textAlign = TextAlign.Center,
            text = ":"
        )
        ListItemPicker(
            value = seconds,
            onValueChange = {
                seconds = it
                setTime(hours, minutes, seconds)
            },
            list = secondValues)
    }
}


@Composable
fun SetTimerDialog(
    setShowDialog: (Boolean) -> Unit,
    setValue: (Int)-> Unit
) {

    var timerTime = 0

    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.time_picker_dialog_title),
                        style = MaterialTheme.typography.h6
                    )

                    TimerPicker(
                        setTime = { hours, minutes, seconds ->
                        timerTime = seconds + 60*minutes + 3600*hours
                        Log.i(">>>>>", "Timer $timerTime")
                    })

                    Spacer(modifier = Modifier.height(20.dp))



                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .weight(1f),
                            onClick = { setShowDialog(false) }
                        ) {
                            Text(text = stringResource(id = R.string.dialog_dismiss))
                        }
                        Button(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .weight(1f),
                            onClick = {
                                setValue(timerTime)
                                setShowDialog(false)
                            },
                        ) {
                            Text(text = stringResource(id = R.string.dialog_OK))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayAlarm(volume: Int = 80) {
    val mediaPlayer = MediaPlayer.create(LocalContext.current, R.raw.alarm)
    val maxVolume = 100.0
    val volumeLog = (1.0 - Math.log(maxVolume - volume)/Math.log(maxVolume)).toFloat()
    mediaPlayer.setVolume(volumeLog, volumeLog)
    mediaPlayer.start()
}
