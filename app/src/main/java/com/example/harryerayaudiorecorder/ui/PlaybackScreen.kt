package com.example.harryerayaudiorecorder.ui

import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.harryerayaudiorecorder.R
import java.io.File

@Composable
fun PlaybackScreen(audioViewModel: AudioViewModel,
                   title: String,
                   duration: Double,
                   fileName: String,
                   fileSize: Double,
                   modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isPlaying = remember { mutableStateOf(false) } // State to track if audio is playing

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            if (isPlaying.value) {
                audioViewModel.stopAudio()
                isPlaying.value = false
            } else {
                val externalFilesDir = context.getExternalFilesDir(null)
                val audioFile = File(externalFilesDir, fileName)  // Adjust the file path and name accordingly.
                if (audioFile.exists()) {
                    audioViewModel.playAudio(audioFile)
                    isPlaying.value = true
                } else {
                    // Handle the case where the file does not exist
                }
            }
        }) {
            Image(
                painter = painterResource(id = if (isPlaying.value) R.drawable.ic_pause else R.drawable.ic_play),
                contentDescription = if (isPlaying.value) "Stop" else "Play",

            )
            Text(if (isPlaying.value) "Pause" else "Play")

//            var waveformProgress by remember { mutableStateOf(0F) }
//            AudioWaveform(
//                amplitudes = amplitudes,
//                progress = waveformProgress,
//                onProgressChange = { waveformProgress = it }
//            )
        }
    }
}
