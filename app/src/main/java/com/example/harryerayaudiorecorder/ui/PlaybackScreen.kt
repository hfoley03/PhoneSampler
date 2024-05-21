package com.example.harryerayaudiorecorder.ui

import AudioViewModel
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.harryerayaudiorecorder.R
import com.linc.audiowaveform.AudioWaveform
import kotlinx.coroutines.delay
import linc.com.amplituda.Amplituda
import linc.com.amplituda.AmplitudaResult
import linc.com.amplituda.exceptions.AmplitudaException
import linc.com.amplituda.exceptions.io.AmplitudaIOException
import java.io.File


@Composable
@Preview(showBackground = true)
fun PlaybackScreenPreview() {

}
@Composable
fun PlaybackScreen(
    audioViewModel: AudioViewModel,
    durationSample: Int,
    fileName: String,
    fileSize: Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isPlaying = remember { mutableStateOf(false) }
    val isRepeatOn = remember { mutableStateOf(false) }
    val audioCapturesDirectory = File(context.getExternalFilesDir(null), "/AudioCaptures")
    val audioFile = File(audioCapturesDirectory.absolutePath, fileName)
    val scope = rememberCoroutineScope()
    var waveformProgress by remember { mutableStateOf(0F) }
    var amplitudesData: List<Int> = listOf()
    var currentPosition by remember { mutableStateOf(0) }
    val showSpeedSlider = remember { mutableStateOf(false) }
    val playbackSpeed = remember { mutableStateOf(1.0f) }

    val amplituda = Amplituda(context)

    /* Step 2: process audio and handle result */
    amplituda.processAudio(audioFile.path)[
        { result: AmplitudaResult<String?> ->
            amplitudesData = result.amplitudesAsList()
            //Log.d("amplitudesData", audioFile.path)
            val amplitudesForFirstSecond =
                result.amplitudesForSecond(1)
            val duration = result.getAudioDuration(AmplitudaResult.DurationUnit.SECONDS)
            val source = result.audioSource
            val sourceType = result.inputAudioType
        }, { exception: AmplitudaException? ->
            if (exception is AmplitudaIOException) {
                println("IO Exception!")
            }
        }
    ]

    LaunchedEffect(isPlaying.value) {
        while (isPlaying.value) {
            currentPosition = audioViewModel.getCurrentPosition() // Convert milliseconds to seconds
            //waveformProgress = (audioViewModel.getCurrentPosition().toFloat() / audioViewModel.getAudioDuration(audioFile).toFloat())
            waveformProgress = (audioViewModel.getCurrentPosition() / durationSample.toFloat())
            delay(20) // Update every second
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = fileName, modifier = Modifier.padding(bottom = 16.dp))

//        Text(text = formatTime(currentPosition), modifier = Modifier.padding(32.dp), fontSize = 64.sp)

        EvenlySpacedText2(text = formatTime(currentPosition))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AudioWaveform(
                amplitudes = amplitudesData,
                progress = waveformProgress,
                progressBrush = SolidColor(MaterialTheme.colorScheme.primary),
                waveformBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                onProgressChange = { newProgress ->
                    waveformProgress = newProgress
                    val newPosition = (newProgress * audioViewModel.getAudioDuration(audioFile)).toLong()
                    audioViewModel.seekTo(newPosition)
                    audioViewModel.adjustPlaybackSpeed(playbackSpeed.value)
                    Log.d("playbackscreen", audioViewModel.getCurrentPosition().toString())
                    isPlaying.value = true
                }
            )
        }

        if (showSpeedSlider.value) {
            Slider(
                value = playbackSpeed.value,
                onValueChange = {
                    playbackSpeed.value = it
                    audioViewModel.setPlaybackSpeed(it)
                },
                valueRange = 0.25f..4.0f,
                modifier = Modifier.padding(16.dp)
            )
        }

        Row(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                isRepeatOn.value = !isRepeatOn.value
                audioViewModel.setLooping(isRepeatOn.value)
            }) {
                Icon(
                    painter = painterResource(id = if (isRepeatOn.value) R.drawable.repeat_on else R.drawable.repeat),
                    contentDescription = "Repeat",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Button(
                onClick = { showSpeedSlider.value = !showSpeedSlider.value },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.speed),
                    contentDescription = "Speed",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Button(onClick = { /* Share functionality here */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.share),
                    contentDescription = "Share",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                audioViewModel.fastRewind(3000)
                audioViewModel.setPlaybackSpeed(playbackSpeed.value)
                isPlaying.value = true
            })
            {
                Icon(
                    painter = painterResource(id = R.drawable.round_fast_rewind_24),
                    contentDescription = "Rewind",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Button(
                onClick = {
                    if (isPlaying.value) {
                        audioViewModel.pauseAudio()
                        isPlaying.value = false
                    } else {
                        if (audioFile.exists()) {
                            val startPosition = audioViewModel.getCurrentPosition().toLong()
                            audioViewModel.playAudio(audioFile, startPosition)
                            audioViewModel.setPlaybackSpeed(playbackSpeed.value)
                            isPlaying.value = true

                        } else {
                            // Handle the case where the file does not exist
                        }
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = if (isPlaying.value) R.drawable.ic_pause2 else R.drawable.round_play_circle),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = if (isPlaying.value) "Stop" else "Play",
                )
            }

            Button(onClick = {
                audioViewModel.fastForward(3000)
                audioViewModel.setPlaybackSpeed(playbackSpeed.value)
                isPlaying.value = true
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.round_fast_forward_24),
                    contentDescription = "Fast Forward",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

fun formatTime(milliseconds: Int): String {
    val minutes = (milliseconds / 1000) / 60
    val seconds = (milliseconds / 1000) % 60
    val centiseconds = (milliseconds / 10) % 100
    return String.format("%02d:%02d:%02d", minutes, seconds, centiseconds)
}

@Composable
fun EvenlySpacedText2(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(32.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (char in text) {
            Text(
                text = char.toString(),
                style = TextStyle(
                    fontSize = 64.sp
                )
            )
        }
    }
}






