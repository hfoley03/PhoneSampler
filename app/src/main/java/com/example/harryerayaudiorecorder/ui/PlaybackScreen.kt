package com.example.harryerayaudiorecorder.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.security.cert.CertPath

@Composable
fun PlaybackScreen(
    title: String,
    duration: Double,
    filePath: String,
    fileSize: Double,
    onPlayButtonClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column {
        Text(text = "$title")
        Text(text = "$duration")
        Text(text = "$filePath")
        Text(text = "$fileSize")
    }

}