package com.example.harryerayaudiorecorder.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun PlaybackScreen(audioViewModel: AudioViewModel,
                   title: String,
                   duration: Double,
                   filePath: String,
                   fileSize: Double,
                   onPlayButtonClicked: () -> Unit = {},
                   modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            val audioFile = File(context.filesDir, "korhan_Yok.wav") // Ensure this file exists
            audioViewModel.playAudio(audioFile)
        }) {
            Text("Play Audio")
        }
        Button(onClick = {
            audioViewModel.stopAudio()
        }) {
            Text("Stop Audio")
        }
    }
}