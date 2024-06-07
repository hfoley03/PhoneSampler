package com.example.harryerayaudiorecorder

import AudioViewModel
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun DownloadNotification(viewModel: AudioViewModel,fileNameFontSize:Int) {
    viewModel.downloadStatusMessage.value?.let { message ->
        Text(
            text = message,
            modifier = Modifier
                .fillMaxWidth()
                .padding(fileNameFontSize.dp),
            color = Color.White,
            textAlign = TextAlign.Center
        )
        LaunchedEffect(message) {
            delay(3000)
            viewModel.clearDownloadStatusMessage()
        }
    }
}