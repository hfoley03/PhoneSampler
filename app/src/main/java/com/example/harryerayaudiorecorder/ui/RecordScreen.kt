package com.example.harryerayaudiorecorder.ui

import AudioViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    modifier: Modifier = Modifier.background(MaterialTheme.colorScheme.background)
) {
    val isRecording by audioViewModel.recorderRunning
    var showBottomSheet by remember { mutableStateOf(false) }  // State to manage BottomSheet visibility

    if (showBottomSheet) {
        BottomSheet(audioViewModel = audioViewModel, onDismiss = { showBottomSheet = false })
    }

    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            StopButton(isRecording, onClick = {
                val timestamp = SimpleDateFormat("dd-MM-yyyy-hh-mm-ss", Locale.ITALY).format(Date())
                val defaultFileName = "SystemAudio-$timestamp"
                audioViewModel.stopRecording(defaultFileName)
                showBottomSheet = true
            })

            RecordButton(onClick = { audioViewModel.startRecording() })
            IconButton(
                onClick = onListButtonClicked,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_menu),
                    contentDescription = "List",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }



}

@Composable
fun RecordButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_record),
            contentDescription = "Record",
            tint = Color.Unspecified,
            )
    }
}

@Composable
fun StopButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        (isRecording)
    ) {

        if (isRecording){
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_delete_disabled),
                contentDescription = "Stop Record",
                tint = MaterialTheme.colorScheme.onBackground
            )
        } else {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_delete),
                contentDescription = "Recorder is not running",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
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



