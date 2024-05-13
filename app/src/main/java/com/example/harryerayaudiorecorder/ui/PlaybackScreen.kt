package com.example.harryerayaudiorecorder.ui

import AudioViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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


//@Composable
//@Preview(showBackground = true)
//fun PlaybackScreenPreview() {
//    Dummy(
//        modifier = Modifier
//    )
//}

@Composable
fun PlaybackScreen(audioViewModel: AudioViewModel,
                   durationSample: Int,
                   fileName: String,
                   fileSize: Double,
                   modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isPlaying = remember { mutableStateOf(false) } // State to track if audio is playing
    lateinit var amplitudesData: List<Int>
    val audioCapturesDirectory = File(context.getExternalFilesDir(null), "/AudioCaptures")
    val audioFile = File(audioCapturesDirectory.absolutePath, fileName)  // Adjust the file path and name accordingly.
    val scope = rememberCoroutineScope()
    var waveformProgress by remember { mutableStateOf(0F) }

//CHANGE THIS TO TWO BUTTONS PAUSE AND PLAY INSTEAD OF ONE BUTTON THAT TOGGLES BETWEEN THE TWO, THINK IT IS EASIER TO TEST
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(text = fileName)
        Button(onClick = {
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
                            delay(100) // Update progress every 100 milliseconds
                        }
                    }
                } else {
                    // Handle the case where the file does not exist
                }
            }
        }) {
            Icon(
                painter = painterResource(id = if (isPlaying.value) R.drawable.ic_pause2 else R.drawable.round_play_circle),
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = if (true) "Stop" else "Play",
            )

            /* Step 1: create Amplituda */
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


        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)){
            AudioWaveform(
                amplitudes = amplitudesData,
                progress = waveformProgress,
                progressBrush = SolidColor(MaterialTheme.colorScheme.primary),
                waveformBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                onProgressChange = { newProgress ->
                    // This code block will execute when the user interacts with the waveform.
                    waveformProgress = newProgress
                    val newPosition = (newProgress * audioViewModel.getAudioDuration(audioFile)).toLong()
                    audioViewModel.seekTo(newPosition) }
            )
        }
    }
}

//@Composable
//fun Dummy(modifier: Modifier = Modifier) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Button(onClick = {
//        }) {
//            Icon(
//                painter = painterResource(id = if (true) R.drawable.ic_pause2 else R.drawable.round_play_circle),
//                tint = MaterialTheme.colorScheme.onPrimary,
//                contentDescription = if (true) "Stop" else "Play",
//                )
//        }
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//                .background(MaterialTheme.colorScheme.background)){
//        }
//
//    }
//}