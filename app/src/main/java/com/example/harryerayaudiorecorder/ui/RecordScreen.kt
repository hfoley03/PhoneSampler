package com.example.harryerayaudiorecorder.ui

import AudioViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.harryerayaudiorecorder.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecordScreen(
    audioViewModel: AudioViewModel,
    onListButtonClicked: () -> Unit,
    //onSettingsButtonClicked: () -> Unit,
    modifier: Modifier = Modifier.background(MaterialTheme.colorScheme.background)
) {
    val isRecording by audioViewModel.recorderRunning
    var showBottomSheet by remember { mutableStateOf(false) }  // State to manage BottomSheet visibility

    if (showBottomSheet) {
        BottomSheet(audioViewModel = audioViewModel, onDismiss = { showBottomSheet = false })
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(3f)
                .fillMaxWidth()
                .padding(16.dp, 8.dp, 16.dp, 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
        ) {MyCanvas()}
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp, 8.dp, 16.dp, 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally)
            {


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    ScalableIconButton(
                        onClick = { },
                        modifier = Modifier.size(64.dp),
                        iconResId = R.drawable.ic_settings
                    )
                    if (isRecording) {
                        ScalableIconButton(
                            onClick = { },
                            modifier = Modifier.size(32.dp),
                            iconResId = R.drawable.ic_record
                        )
                    } else {
                        ScalableIconButton(
                            onClick = { audioViewModel.startRecording() },
                            modifier = Modifier.size(64.dp),
                            iconResId = R.drawable.ic_record
                        )
                    }
                    if (isRecording) {
                        ScalableIconButton(
                            onClick = {
                                val timestamp = SimpleDateFormat(
                                    "dd-MM-yyyy-hh-mm-ss",
                                    Locale.ITALY
                                ).format(Date())
                                val defaultFileName = "SystemAudio-$timestamp"
                                audioViewModel.stopRecording(defaultFileName)
                                showBottomSheet = true
                            },
                            modifier = Modifier.size(64.dp),
                            iconResId = R.drawable.ic_stop
                        )
                    } else {
                        ScalableIconButton(
                            onClick = onListButtonClicked,
                            modifier = Modifier.size(64.dp),
                            iconResId = R.drawable.ic_menu
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun ScalableIconButton(
    onClick: () -> Unit,
    modifier: Modifier,
    iconResId: Int
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = iconResId),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun BottomSheet(audioViewModel: AudioViewModel, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf(audioViewModel.currentFileName.value ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("File Name") },
        text = {
            TextField(
                value = text,
                onValueChange = { newText -> text = newText },
                label = { Text("New File Name") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    audioViewModel.currentFileName.value?.let { currentFileName ->
                        audioViewModel.renameFile(currentFileName, text)
                    }
                    audioViewModel.currentFileName.value?.let { audioViewModel.save(it) }
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MyCanvas() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Draw a red circle
        drawCircle(
            color = Color.Red,
            radius = 100f,
            center = this.center
        )

        // Draw a blue rectangle
        drawRect(
            color = Color.Red,
            topLeft = this.center.copy(x = this.center.x - 50, y = this.center.y - 50),
            size = size / 4f
        )

        // Draw a green line
        drawLine(
            color = Color.Green,
            start = this.center,
            end = this.center.copy(x = this.size.width, y = this.size.height),
            strokeWidth = 5.dp.toPx()
        )
    }
}
