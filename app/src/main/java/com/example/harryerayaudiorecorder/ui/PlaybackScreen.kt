package com.example.harryerayaudiorecorder.ui

import AudioViewModel
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.harryerayaudiorecorder.R
import com.example.harryerayaudiorecorder.shareAudioFile
import com.linc.audiowaveform.AudioWaveform
import kotlinx.coroutines.delay
import linc.com.amplituda.Amplituda
import linc.com.amplituda.AmplitudaResult
import linc.com.amplituda.exceptions.AmplitudaException
import linc.com.amplituda.exceptions.io.AmplitudaIOException
import java.io.File


@Composable
@Preview(showBackground = true)
fun PlaybackScreenPreview() {

}
@Composable
fun PlaybackScreen(
    audioViewModel: AudioViewModel,
    durationSample: Int,
    fileName: String,
    fileSize: Double,
    windowSizeClass: WindowSizeClass,
    onEditButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
//    val isPlaying = remember { mutableStateOf(false) }
//    val isRepeatOn = remember { mutableStateOf(false) }
    var isPlaying by rememberSaveable { mutableStateOf(false) }
    var isRepeatOn by rememberSaveable { mutableStateOf(false) }
    val audioCapturesDirectory = File(context.getExternalFilesDir(null), "/AudioCaptures")
    val audioFile = File(audioCapturesDirectory.absolutePath, fileName)
//    val scope = rememberCoroutineScope()
    var waveformProgress by rememberSaveable { mutableStateOf(0F) }
    var amplitudesData: List<Int> = listOf()
    var currentPosition by rememberSaveable { mutableStateOf(0) }
    val showSpeedSlider = rememberSaveable { mutableStateOf(false) }
    val playbackSpeed = rememberSaveable { mutableStateOf(1.0f) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val boxPadding = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 16.dp
        WindowWidthSizeClass.Medium -> 24.dp
        WindowWidthSizeClass.Expanded -> 32.dp
        else -> 12.dp
    }



    val (iconSize, textSize, lineHeight) = getIconAndTextSize(windowSizeClass = windowSizeClass, isLandscape = isLandscape)
    val displayedTextLengthLandscape = when {
        isTablet() -> 35
        else -> 31
    }
    val displayedTextLengthPortrait = when {
        isTablet() -> 53
        else -> 35
    }

    val amplituda = Amplituda(context)

    amplituda.processAudio(audioFile.path)[
        { result: AmplitudaResult<String?> ->
            amplitudesData = result.amplitudesAsList()
        }, { exception: AmplitudaException? ->
            if (exception is AmplitudaIOException) {
                println("IO Exception!")
            }
        }
    ]

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = audioViewModel.getCurrentPosition()
            waveformProgress = (audioViewModel.getCurrentPosition() / durationSample.toFloat())
            delay(20)
        }
    }
    if(isLandscape){
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxHeight()
                        .padding(
                            PaddingValues(
                                start = 16.dp,
                                top = 8.dp,
                                end = 8.dp,
                                bottom = 8.dp
                            )
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                        EvenlySpacedText2(text = audioViewModel.formatDurationCantiSec(currentPosition))
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            AudioWaveform(
                                amplitudes = amplitudesData,
                                progress = waveformProgress,
                                progressBrush = SolidColor(MaterialTheme.colorScheme.onPrimary),
                                waveformBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                onProgressChange = { newProgress ->
                                    waveformProgress = newProgress
                                    val newPosition =
                                        (newProgress * audioViewModel.getAudioDuration(audioFile)).toLong()
                                    audioViewModel.playAudio(audioFile, newPosition)
                                    audioViewModel.adjustPlaybackSpeed(playbackSpeed.value)
                                    isPlaying = true
                                }
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxHeight()
                        .padding(
                            PaddingValues(
                                start = 8.dp,
                                top = 8.dp,
                                end = 16.dp,
                                bottom = 8.dp
                            )
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(16.dp),
                        Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (showSpeedSlider.value) {
                            Slider(
                                value = playbackSpeed.value,
                                onValueChange = {
                                    playbackSpeed.value = it
                                    audioViewModel.setPlaybackSpeed(it)
                                },
                                valueRange = 0.25f..4.0f,
                                modifier = Modifier.padding(16.dp).fillMaxWidth()
                            )
                        }
                        else{
                            val displayedFileName = if (fileName.length > displayedTextLengthLandscape) fileName.substring(0, displayedTextLengthLandscape) + "…" else fileName
                            Text(
                                text = displayedFileName,
                                fontSize = textSize.value.sp,
                                lineHeight = lineHeight.value.sp,
                                modifier = Modifier.padding(16.dp).fillMaxWidth()
                            )
                        }

                        Row(
                            modifier = Modifier
                                .padding(start = boxPadding, end = boxPadding, bottom = boxPadding/2)
                                .align(Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(onClick = {
                                isRepeatOn = !isRepeatOn
                                audioViewModel.setLooping(isRepeatOn)
                            }) {
                                Icon(
                                    painter = painterResource(id = if (isRepeatOn) R.drawable.repeat_on else R.drawable.repeat),
                                    contentDescription = "Repeat",
                                    modifier = Modifier.size(iconSize),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            Button(
                                onClick = { showSpeedSlider.value = !showSpeedSlider.value },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.speed),
                                    contentDescription = "Speed",
                                    modifier = Modifier.size(iconSize),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            Button(onClick = {
                                shareAudioFile(context, audioFile)
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.share),
                                    contentDescription = "Share",
                                    modifier = Modifier.size(iconSize),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .padding(start = boxPadding, end = boxPadding, top = boxPadding/2)
                                .align(Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(onClick = {
                                audioViewModel.fastRewind((durationSample * 0.1f).toInt())
                                audioViewModel.setPlaybackSpeed(playbackSpeed.value)
                                isPlaying = true
                            })
                            {
                                Icon(
                                    painter = painterResource(id = R.drawable.round_fast_rewind_24),
                                    contentDescription = "Rewind",
                                    modifier = Modifier.size(iconSize),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            Button(
                                onClick = {
                                    if (isPlaying) {
                                        audioViewModel.pauseAudio()
                                        isPlaying = false
                                    } else {
//                                        if (audioFile.exists()) {
                                        if (true) {
                                            val startPosition = currentPosition.toLong()

                                            audioViewModel.playAudio(audioFile, startPosition)
                                            audioViewModel.setPlaybackSpeed(playbackSpeed.value)
                                            isPlaying = true

                                        } else {
                                            // file does not exist
                                        }
                                    }
                                },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = if (isPlaying) R.drawable.ic_pause2 else R.drawable.round_play_circle),
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(iconSize),
                                    contentDescription = if (isPlaying) "Stop" else "Play",
                                )
                            }

                            Button(onClick = {
                                audioViewModel.fastForward((durationSample * 0.1f).toInt())
                                audioViewModel.setPlaybackSpeed(playbackSpeed.value)
                                isPlaying = true
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.round_fast_forward_24),
                                    contentDescription = "Fast Forward",
                                    modifier = Modifier.size(iconSize),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }



    else {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp, 16.dp, 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ){
                        EvenlySpacedText2(text = audioViewModel.formatDurationCantiSec(currentPosition))
                    }
                    Box(
                        modifier = Modifier
                        .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        AudioWaveform(
                            amplitudes = amplitudesData,
                            progress = waveformProgress,
                            progressBrush = SolidColor(MaterialTheme.colorScheme.onPrimary),
                            waveformBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            onProgressChange = { newProgress ->
                                waveformProgress = newProgress
                                val newPosition =
                                    (newProgress * audioViewModel.getAudioDuration(audioFile)).toLong()
//                                audioViewModel.seekTo(newPosition)
                                audioViewModel.playAudio(audioFile, newPosition)
                                audioViewModel.adjustPlaybackSpeed(playbackSpeed.value)
                                isPlaying = true
                            }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp, 16.dp, 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ){
                if (showSpeedSlider.value) {
                    Slider(
                        value = playbackSpeed.value,
                        onValueChange = {
                            playbackSpeed.value = it
                            audioViewModel.setPlaybackSpeed(it)
                        },
                        valueRange = 0.25f..4.0f,
                        modifier = Modifier.padding(8.dp).testTag("SpeedSlider")
                    )
                }
                else {
                    val displayedFileName = if (fileName.length > displayedTextLengthPortrait) fileName.substring(0, displayedTextLengthPortrait) + "…" else fileName
                    Text(text = displayedFileName,
                        fontSize = textSize.value.sp,
                        lineHeight = lineHeight.value.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp, 16.dp, 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            isRepeatOn = !isRepeatOn
                            audioViewModel.setLooping(isRepeatOn)
                        }) {
                            Icon(
                                painter = painterResource(id = if (isRepeatOn) R.drawable.repeat_on else R.drawable.repeat),
                                contentDescription = "Repeat",
                                modifier = Modifier.size(iconSize),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        Button(
                            onClick = { showSpeedSlider.value = !showSpeedSlider.value },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.speed),
                                contentDescription = "Speed",
                                modifier = Modifier.size(iconSize),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        Button(onClick = {
                            shareAudioFile(context, audioFile)

                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.share),
                                contentDescription = "Share",
                                modifier = Modifier.size(iconSize),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            audioViewModel.fastRewind((durationSample * 0.1f).toInt())
                            audioViewModel.setPlaybackSpeed(playbackSpeed.value)
                            isPlaying = true
                        })
                        {
                            Icon(
                                painter = painterResource(id = R.drawable.round_fast_rewind_24),
                                contentDescription = "Rewind",
                                modifier = Modifier.size(iconSize),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        Button(
                            onClick = {
                                if (isPlaying) {


                                    audioViewModel.pauseAudio()
                                    isPlaying = false
                                    context.unlockOrientation()

                                } else {
                                    if (true) {

                                        val startPosition = currentPosition.toLong()
                                        audioViewModel.playAudio(audioFile, startPosition)
                                        audioViewModel.setPlaybackSpeed(playbackSpeed.value)
                                        isPlaying = true
                                        context.lockOrientation()


                                    } else {
                                        // Handle the case where the file does not exist
                                    }
                                }
                            },
                        ) {
                            Icon(
                                painter = painterResource(id = if (isPlaying) R.drawable.ic_pause2 else R.drawable.round_play_circle),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(iconSize),
                                contentDescription = if (isPlaying) "Stop" else "Play",
                            )
                        }

                        Button(onClick = {
                            audioViewModel.fastForward((durationSample * 0.1f).toInt())
                            audioViewModel.setPlaybackSpeed(playbackSpeed.value)
                            isPlaying = true
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.round_fast_forward_24),
                                contentDescription = "Fast Forward",
                                modifier = Modifier.size(iconSize),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun isTablet(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.smallestScreenWidthDp >= 600
}

@Composable
fun getIconAndTextSize(windowSizeClass: WindowSizeClass, isLandscape: Boolean): Triple<Dp, Dp, Dp> {
    val tablet = isTablet()
    val iconSize = when {
        tablet -> when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> 74.dp
            else -> 44.dp
        }
        isLandscape -> when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Expanded -> 30.dp
            else -> 28.dp
        }
        else -> when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 32.dp
            WindowWidthSizeClass.Medium -> 45.dp
            WindowWidthSizeClass.Expanded -> 58.dp
            else -> 35.dp
        }
    }


    val textSize = when {
        tablet && isLandscape -> 42.dp
        tablet && !isLandscape -> 46.dp
        !tablet && isLandscape -> 24.dp
        !tablet && !isLandscape -> 24.dp
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium -> 34.dp  // for medium devices
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded -> 36.dp  // for Slightly larger for larger screens
        else -> 32.dp
    }

    val lineHeight = when {
        tablet -> 48.dp
        else -> 30.dp
    }

    return Triple(iconSize, textSize, lineHeight)
}




@Composable
fun EvenlySpacedText2(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(32.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (char in text) {
            Text(
                text = char.toString(),
                style = TextStyle(
                    fontSize = 64.sp
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

fun Context.lockOrientation() {
    (this as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
}

fun Context.unlockOrientation() {
    (this as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
}




