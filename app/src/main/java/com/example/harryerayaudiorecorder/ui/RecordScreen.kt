package com.example.harryerayaudiorecorder.ui

import AudioViewModel
import android.content.res.Configuration
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
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.harryerayaudiorecorder.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecordScreen(
    audioViewModel: AudioViewModel,
    onListButtonClicked: () -> Unit,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier.background(MaterialTheme.colorScheme.background)
) {
    val isRecording by audioViewModel.recorderRunning
    var showBottomSheet by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val boxPadding = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 8.dp
        WindowWidthSizeClass.Medium -> 12.dp
        WindowWidthSizeClass.Expanded -> 16.dp
        else -> 12.dp
    }

    val iconSize = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 64.dp
        WindowWidthSizeClass.Medium -> 72.dp
        WindowWidthSizeClass.Expanded -> 96.dp
        else -> 84.dp
    }

    LayoutForOrientation(
        isLandscape,
        boxPadding,
        iconSize,
        isRecording,
        audioViewModel,
        onListButtonClicked,
        setShowBottomSheet = { showBottomSheet = it } // Passing the setter function
    )

    if (showBottomSheet) {
        BottomSheet(audioViewModel = audioViewModel, onDismiss = { showBottomSheet = false })
    }
}

@Composable
fun LayoutForOrientation(
    isLandscape: Boolean,
    boxPadding: Dp,
    iconSize: Dp,
    isRecording: Boolean,
    audioViewModel: AudioViewModel,
    onListButtonClicked: () -> Unit,
    setShowBottomSheet: (Boolean) -> Unit
) {
    if (isLandscape) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(all = boxPadding)
                    .clip(RoundedCornerShape(boxPadding))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                MyBoxContent(iconSize, isRecording, audioViewModel, onListButtonClicked, isLandscape=true, setShowBottomSheet, Modifier.fillMaxHeight())
            }

            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
                    .padding(all = boxPadding)
                    .clip(RoundedCornerShape(boxPadding))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
            ) {
                MyCanvas(Modifier.fillMaxHeight())

            }

        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
                    .padding(all = boxPadding)
                    .clip(RoundedCornerShape(boxPadding))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ){
                MyCanvas(Modifier.fillMaxWidth())
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(all = boxPadding)
                    .clip(RoundedCornerShape(boxPadding))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ){
                MyBoxContent(iconSize, isRecording, audioViewModel, onListButtonClicked, isLandscape = false, setShowBottomSheet, Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun MyBoxContent(
    iconSize: Dp,
    isRecording: Boolean,
    audioViewModel: AudioViewModel,
    onListButtonClicked: () -> Unit,
    isLandscape: Boolean,
    setShowBottomSheet: (Boolean) -> Unit,
    modifier: Modifier
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ControlButtonsRow(iconSize, isRecording, audioViewModel, onListButtonClicked, isLandscape, setShowBottomSheet)
    }

}
@Composable
fun ControlButtonsRow(
    iconSize: Dp,
    isRecording: Boolean,
    audioViewModel: AudioViewModel,
    onListButtonClicked: () -> Unit,
    isLandscape: Boolean,
    setShowBottomSheet: (Boolean) -> Unit
) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 1.dp),
//        horizontalArrangement = Arrangement.SpaceEvenly
//    )
    ColumnOrRow(isLandscape = isLandscape) {
        ScalableIconButton(
            onClick = { },
            modifier = Modifier.size(iconSize),
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
                modifier = Modifier.size(iconSize),
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
                    setShowBottomSheet(true)
                },
                modifier = Modifier.size(iconSize),
                iconResId = R.drawable.ic_stop
            )
        } else {
            ScalableIconButton(
                onClick = onListButtonClicked,
                modifier = Modifier.size(iconSize),
                iconResId = R.drawable.ic_menu
            )
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
    var showMaxLengthWarning by remember { mutableStateOf(false)}

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("File Name") },
        text = {
            Column {
                TextField(
                    value = text,
                    onValueChange = { newText ->
                        if (newText.length <= 50) {
                            text = newText
                            showMaxLengthWarning = false
                        } else {
                            showMaxLengthWarning = true
                        }

                    },
                    label = { Text("New File Name")
                    }
                )

                if (showMaxLengthWarning) {
                    Text(
                        text = "Maximum file name size exceeded. Only 50 characters allowed.",
                        color = Color.Red,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

            }


        },
        confirmButton = {
            Button(
                onClick = {
                    if (!showMaxLengthWarning && text.isNotEmpty()) {
                        audioViewModel.currentFileName.value?.let { currentFileName ->
                            audioViewModel.renameFile(text)
                        }
                        audioViewModel.currentFileName.value?.let { audioViewModel.save(it) }                    }

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
fun ColumnOrRow(
    isLandscape: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (isLandscape) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize()
        ) {
            content()
        }
    } else {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxSize()
        ) {
            content()
        }
    }
}

@Composable
fun MyCanvas(modifier: Modifier) {
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
