package com.example.harryerayaudiorecorder.ui

import AudioViewModel
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.harryerayaudiorecorder.R
import com.linc.audiowaveform.AudioWaveform
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    val amplituda = Amplituda(context)

    /* Step 2: process audio and handle result */
    amplituda.processAudio(audioFile.path)[
        { result: AmplitudaResult<String?> ->
            amplitudesData = result.amplitudesAsList()
            Log.d("amplitudesData", audioFile.path)
            val amplitudesForFirstSecond =
                result.amplitudesForSecond(1)
            val duration = result.getAudioDuration(AmplitudaResult.DurationUnit.SECONDS)
            val source = result.audioSource
            val sourceType = result.inputAudioType
        }, { exception: AmplitudaException? ->
            if (exception is AmplitudaIOException) {
                println("IO Exception!")
            }
        }]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = fileName, modifier = Modifier.padding(bottom = 16.dp))

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
                }
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
                audioViewModel.setRepeatMode(isRepeatOn.value)
            }) {
                Icon(
                    painter = painterResource(id = if (isRepeatOn.value) R.drawable.repeat_on else R.drawable.repeat),
                    contentDescription = "Repeat",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Button(onClick = { /* Speed functionality here */ },
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
            Button(onClick = { /* Fast rewind functionality here */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.round_fast_rewind_24),
                    contentDescription = "Rewind",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Button(
                onClick = {
                    if (isPlaying.value) {
                        audioViewModel.stopAudio()
                        isPlaying.value = false
                    } else {
                        if (audioFile.exists()) {
                            audioViewModel.playAudio(audioFile)
                            isPlaying.value = true
                            scope.launch {
                                while (isPlaying.value) {
                                    waveformProgress = (audioViewModel.getCurrentPosition() / durationSample.toFloat())
                                    delay(100)
                                }
                            }
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

            Button(onClick = { /* Fast forward functionality here */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.round_fast_forward_24),
                    contentDescription = "Fast Forward",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}




