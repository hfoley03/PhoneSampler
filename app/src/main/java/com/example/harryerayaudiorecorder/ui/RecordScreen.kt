package com.example.harryerayaudiorecorder.ui

import AudioViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
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



            val configuration = LocalConfiguration.current
            val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),

                contentAlignment = Alignment.Center ,// Center the content inside the Box

            ) {
                val boxSize = if (isLandscape) maxHeight / 2 else maxWidth / 2

                Column(
                    modifier = Modifier
                        .size(boxSize * 2)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        ScalableIconButton(
                            onListButtonClicked,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            iconResId = R.drawable.ic_settings
                        )
                        ScalableIconButton(
                            onListButtonClicked,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            iconResId = R.drawable.ic_menu
                        )
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        StopButton(isRecording, onClick = {
                            val timestamp = SimpleDateFormat("dd-MM-yyyy-hh-mm-ss", Locale.ITALY).format(Date())
                            val defaultFileName = "SystemAudio-$timestamp"
                            audioViewModel.stopRecording(defaultFileName)
                            showBottomSheet = true
                        },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                        RecordButton(
                            onClick = { audioViewModel.startRecording() },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    }

            }
        }

}

@Composable
fun RecordButton(
    onClick: () -> Unit,
    modifier: Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_record),
            contentDescription = "Record",
            tint = Color.Unspecified,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun StopButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
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
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxSize()

            )
        } else {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_delete),
                contentDescription = "Recorder is not running",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxSize()
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
//
//@Composable
//fun ScalableIconButtons() {
//    BoxWithConstraints(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        val boxSize = maxWidth / 2
//
//        Column(
//            modifier = Modifier
//                .size(boxSize * 2)
//        ) {
//            Row(
//                modifier = Modifier
//                    .weight(1f)
//            ) {
//                ScalableIconButton(
//                    modifier = Modifier
//                        .weight(1f)
//                        .aspectRatio(1f),
//                    iconResId = R.drawable.ic_settings
//                )
//                ScalableIconButton(
//                    modifier = Modifier
//                        .weight(1f)
//                        .aspectRatio(1f),
//                    iconResId = R.drawable.ic_menu
//                )
//            }
//            Row(
//                modifier = Modifier
//                    .weight(1f)
//            ) {
//                ScalableIconButton(
//                    modifier = Modifier
//                        .weight(1f)
//                        .aspectRatio(1f),
//                    iconResId = R.drawable.ic_delete
//                )
//                ScalableIconButton(
//                    modifier = Modifier
//                        .weight(1f)
//                        .aspectRatio(1f),
//                    iconResId = R.drawable.ic_record
//                )
//            }
//        }
//    }
//}

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

