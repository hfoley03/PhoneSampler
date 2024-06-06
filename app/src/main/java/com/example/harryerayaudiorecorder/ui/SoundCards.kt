package com.example.harryerayaudiorecorder.ui

import AudioViewModel
import android.content.Context
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.harryerayaudiorecorder.data.FreesoundSoundCard
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.example.harryerayaudiorecorder.R
import com.example.harryerayaudiorecorder.authenticate
import com.example.harryerayaudiorecorder.data.SoundCard
import java.io.File


@Composable
fun FsSoundCard(sound: FreesoundSoundCard,
                fileNameFontSize:Int,
                audioViewModel: AudioViewModel,
                audioCapturesDirectory: File,
                downloadTrigger: Boolean,
                setDownloadTrigger: (Boolean) -> Unit) {
    val context = LocalContext.current
    val isPlaying = audioViewModel.getPlayingState(sound.id)
    var showOAuthWebView by remember { mutableStateOf(false) }
    var accessToken = audioViewModel.getAccessToken(context)

    if (showOAuthWebView) {
        authenticate(
            audioViewModel = audioViewModel,
            setShowOAuthWebView = { showOAuthWebView = it },
            context = context,
            onAuthenticated = { token ->
                accessToken = audioViewModel.getAccessToken(context)
                showOAuthWebView = false
                if (token != null) {
                    //download if authenticated
                    audioViewModel.downloadSound(
                        sound.id.toString(),
                        accessToken!!,
                        sound.name,
                        audioCapturesDirectory,
                        downloadTrigger,
                        setDownloadTrigger,
                        context
                    )
                }
            }
        )
    }

    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp, 16.dp, 8.dp)

    ) {
        Column(
            modifier = Modifier.padding((fileNameFontSize / 2.0).toInt().dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = sound.name,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = fileNameFontSize.sp,
                    lineHeight = fileNameFontSize.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column {

                    IconButton(onClick = {
                        audioViewModel.togglePlayPause(sound)
                    }) {
                        if (isPlaying.value) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_stop),
                                contentDescription = "Stop",
                                modifier = Modifier.size((fileNameFontSize * 1.5).toInt().dp)                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                modifier = Modifier.size((fileNameFontSize * 1.5).toInt().dp)                            )
                        }
                    }
                    IconButton(onClick = {
                        if (accessToken == null) {
                            showOAuthWebView = true
                        }else{
                            audioViewModel.downloadSound(
                                sound.id.toString(),
                                accessToken!!,
                                sound.name,
                                audioCapturesDirectory,
                                downloadTrigger,
                                setDownloadTrigger,
                                context
                            )
                        }

                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.download_icon),
                            contentDescription = "Download",
                            modifier = Modifier.size((fileNameFontSize * 1.5).toInt().dp)
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "Description",
                            modifier = Modifier.size((fileNameFontSize * 1.5).toInt().dp)
                        )
                    }

                }
            }

            Text(
                text = "Duration: ${audioViewModel.formatDurationCantiSec((sound.duration*1000).toInt())} ",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = fileNameFontSize.sp
            )

            Text(
                text = "File Size:  ${String.format("%.2f", sound.filesize / 1_000_000.0)} MB",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = fileNameFontSize.sp
            )
        }
    }
}


@Composable
fun SoundRecordingCard(
    audioViewModel: AudioViewModel,
    soundCard: SoundCard,
    audioCapturesDirectory: File,
    fileNameFontSize: Int,
    onClick: () -> Unit,
    onPencilClicked: (String) -> Unit,
    onDeleteClick: () -> Unit,
    context: Context
) {
    var showEditFileNameDialog by remember { mutableStateOf(false) }
    var showUploadDialog by remember { mutableStateOf(false) }
    var showOAuthWebView by remember { mutableStateOf(false) }
    var accessToken = audioViewModel.getAccessToken(context)
    var showMenu by remember { mutableStateOf(false) }

    if (showEditFileNameDialog) {
        FileNameEditDialog(
            soundCard = soundCard,
            onFileNameChange = { newFileName ->
                audioViewModel.renameFileFromList(soundCard.fileName, newFileName)
                onPencilClicked(newFileName)
                showEditFileNameDialog = false
            }
        ) { showEditFileNameDialog = false }
    }

    if (showOAuthWebView) {
        authenticate(
            audioViewModel = audioViewModel,
            setShowOAuthWebView = { showOAuthWebView = it },
            context = context,
            onAuthenticated = { token ->
                accessToken = audioViewModel.getAccessToken(context)
                showOAuthWebView = false
                if (token != null) {
                    showUploadDialog = true
                }
            }
        )
    }

    if (showUploadDialog) {
        UploadSoundDialog(
            onDismiss = { showUploadDialog = false },
            onConfirm = { tags, description, license, pack, geotag ->
                if (accessToken != null) {
                    Log.d("filename", audioCapturesDirectory.absolutePath + "/" + soundCard.fileName)
//                    Log.d("accessToken", accessToken)
                    Log.d("License Info", "License: $license")
                    audioViewModel.uploadSound(
                        accessToken!!,
                        File(audioCapturesDirectory.absolutePath + "/" + soundCard.fileName),
                        name = soundCard.fileName,
                        tags = tags,
                        description = description,
                        license = license,
                        pack = pack,
                        geotag = geotag
                    )
                }
            }
        )
    }

    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .testTag("SoundCard") // Adding test tag here
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { }, // Handle long press
                    onTap = { onClick() } // Handle single tap
                )
            }
    ) {
        Column(
            modifier = Modifier.padding((fileNameFontSize / 2.0).toInt().dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = soundCard.fileName,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = fileNameFontSize.sp,
                    lineHeight = fileNameFontSize.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column {

                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert, // This is the three dots vertical icon
                            contentDescription = "More Options",
                            modifier = Modifier.size((fileNameFontSize * 1.5).toInt().dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                showMenu = false
                                showEditFileNameDialog = true
                            },
                            text = { Text("Edit Title") }
                        )
                        DropdownMenuItem(
                            onClick = {
                                showMenu = false
                                onDeleteClick()
                            },
                            text = { Text("Delete") }
                        )
                        DropdownMenuItem(
                            onClick = {
                                showMenu = false
                                 if (accessToken == null) {
                                     showOAuthWebView = true
                                 } else {
                                     showUploadDialog = true
                                 }
                            },
                            text = { Text("Upload to Freesound") }
                        )
                    }
                }
            }

            Text(
                text = "${audioViewModel.formatDurationCantiSec(soundCard.duration.toInt())}",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = (fileNameFontSize / 1.3).toInt().sp
            )
            Text(
                text = "${String.format("%.2f", soundCard.fileSize)} MB",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = (fileNameFontSize / 1.3).toInt().sp
            )
            Text(
                text = "${soundCard.date}",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = (fileNameFontSize / 1.3).toInt().sp
            )
        }
    }
}