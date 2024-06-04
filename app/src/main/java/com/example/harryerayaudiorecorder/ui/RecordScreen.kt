package com.example.harryerayaudiorecorder.ui

import AudioViewModel
import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.harryerayaudiorecorder.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin


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
        WindowWidthSizeClass.Compact -> 16.dp // harry increased this from 8 to 16 to match the other screens on my phone
        WindowWidthSizeClass.Medium -> 24.dp    // i have doubled these from what you had to match, please check on tablet
        WindowWidthSizeClass.Expanded -> 32.dp
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
                    .weight(3f)
                    .fillMaxWidth()
                    .padding(boxPadding / 2, boxPadding / 4, boxPadding / 2, boxPadding / 4)   //

                    .clip(RoundedCornerShape(boxPadding / 2))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
            ) {
                MyBezierCanvas(Modifier.fillMaxHeight(), isRecording, isLandscape)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(boxPadding / 2, boxPadding / 4, boxPadding / 2, boxPadding / 4)   //
                    .clip(RoundedCornerShape(boxPadding / 2))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                MyBoxContent(iconSize, isRecording, audioViewModel, onListButtonClicked, isLandscape=true, setShowBottomSheet, Modifier.fillMaxHeight())
            }



        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
                    .padding(boxPadding, boxPadding / 2, boxPadding, boxPadding / 2)
                    .clip(RoundedCornerShape(boxPadding))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ){
                MyBezierCanvas(Modifier.fillMaxWidth(), isRecording, isLandscape)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(boxPadding, boxPadding / 2, boxPadding, boxPadding / 2)
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
                onClick = { audioViewModel.stopWithoutSavingRecording()},
                modifier = Modifier.size(iconSize),
                iconResId = R.drawable.ic_delete
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
fun MyBezierCanvas(modifier: Modifier = Modifier, isRecording: Boolean, isLandscape: Boolean ) {
    // State to control the animation progress
    val infiniteTransition = rememberInfiniteTransition()
    val direction = if (isRecording) -1 else +1
    val animationProgressSpeed1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val animationProgressSpeed2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Only run the animation if recording is true
    val progress = if (isRecording) animationProgressSpeed1 else animationProgressSpeed2
    Box(
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val halfHeight = canvasHeight / 2
            val halfWidth = canvasWidth / 2
            val d1 = if (isLandscape) canvasWidth / 5 else canvasHeight / 5
            val d2 = if (isLandscape) canvasWidth / 4 else canvasHeight / 2
            val alphaFloat = 0.2f


            // Points p1 and p2 are located halfway down the canvas
            val p0 = if (isLandscape) Offset(halfWidth - d1, halfHeight) else Offset(
                halfWidth,
                halfHeight - d1
            )
            val p3 = if (isLandscape) Offset(halfWidth + d1, halfHeight) else Offset(
                halfWidth,
                halfHeight + d1
            )

            // Calculate p0 and p3 positions based on sine and cosine functions
            val angle = progress * 2 * Math.PI
            val p1 = Offset(
                x = p0.x - 1 * direction * d2 * cos(angle).toFloat(),
                y = p0.y + d2 * sin(angle).toFloat()
            )
            val p2 = Offset(
                x = p3.x + direction * d2 * cos(angle).toFloat(),
                y = p3.y + d2 * sin(angle).toFloat()
            )

            // Draw the Bézier curve using the control points p0, p1, p2, and p3
            val path = Path().apply {
                moveTo(p0.x, p0.y)
                cubicTo(
                    p1.x, p1.y,
                    p2.x, p2.y,
                    p3.x, p3.y
                )
            }

            drawPath(
                path,
                color = Color.White,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 20f),
                alpha = alphaFloat
            )

            // Optionally, draw the control points for visualization
            drawCircle(Color.White, radius = 50f, center = p0, alpha = alphaFloat)
//        drawCircle(Color.Black, radius = 10f, center = p1)
//        drawCircle(Color.Black, radius = 10f, center = p2)
            drawCircle(Color.White, radius = 50f, center = p3, alpha = alphaFloat)
        }
        val msg = if (isRecording) "recording" else "ready"
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = msg, fontSize = 32.sp)
            Text(text = "ti::me::er", fontSize = 32.sp)
        }
    }
}

