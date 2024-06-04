package com.example.harryerayaudiorecorder.ui

import AudioViewModel
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.harryerayaudiorecorder.ApiResponseDialog
import com.example.harryerayaudiorecorder.OAuthWebViewScreen
import com.example.harryerayaudiorecorder.R
import com.example.harryerayaudiorecorder.TokenResponse
import com.example.harryerayaudiorecorder.authenticate
import com.example.harryerayaudiorecorder.data.SoundCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecordingsListScreen(
    audioViewModel: AudioViewModel,
    onSongButtonClicked: (SoundCard) -> Unit,
    onThreeDotsClicked: (String) -> Unit,
    modifier: Modifier = Modifier
)  {
    val context = LocalContext.current
    val audioCapturesDirectory = File(context.getExternalFilesDir(null), "/AudioCaptures")
    val soundCardList = remember { mutableStateListOf<MutableState<SoundCard>>() }
    val fileNameFontSize = when {
        SamplerViewModel().isTablet() -> 32
        else -> 22
    }
    var showOAuthWebView by remember { mutableStateOf(false) }
    val accessToken = remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val soundRecordDatabaseList = audioViewModel.getAllAudioRecords()

            soundRecordDatabaseList.forEach { record ->
                val soundCard = SoundCard(
                    duration = record.duration,
                    fileName = record.filename,
                    fileSize = record.fileSize,
                    date = record.date
                )
                Log.d("Created SoundCard", "${soundCard.fileName}")

                withContext(Dispatchers.Main) {
                    soundCardList.add(mutableStateOf(soundCard))
                }
            }
        }


        accessToken.value = audioViewModel.getAccessToken(context)

    }

    val filteredSoundCards = if (searchText.isBlank()) {
        soundCardList
    } else {
        soundCardList.filter {
            it.value.fileName.contains(searchText, ignoreCase = true)
        }
    }

    IconButton(onClick = {  }) {
        Icon(
            Icons.Default.Search,
            contentDescription = "Search",
            modifier = Modifier.size((fileNameFontSize * 1.5).toInt().dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = { Text("Search Recordings") },
                            trailingIcon =
                            {
                                IconButton(onClick = { /* Implemented from the callback */ }) {
                                Icon(
                                    Icons.Default.Search,
                                    modifier = Modifier.size((fileNameFontSize*1.5).dp),
                                    contentDescription = "Local Search"
                                )
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = { /* Implement internet search functionality */ },
                            modifier = Modifier.padding(end = (fileNameFontSize).dp)
                            ) {
                            Icon(
                                painter = painterResource(id = R.drawable.internet_browsing_icon),
                                modifier = Modifier.size((fileNameFontSize*1.5).dp),
                                contentDescription = "Search on Internet Icon",
                            )

                        }
                    }
                },
                actions = {
                    // Other actions can still be added here if needed
                },
            )
        }
    ){
        if (showOAuthWebView) {
            authenticate(
                audioViewModel = audioViewModel,
                setShowOAuthWebView = { showOAuthWebView = it },
                context = context,
                onAuthenticated = { token ->
                    accessToken.value = token
                    showOAuthWebView = false
                }
            )
        } else {
            LazyColumn(modifier = Modifier.padding(top = (fileNameFontSize*2).dp)) {
                items(filteredSoundCards) { item ->
                    SoundRecordingCard(
                        audioViewModel,
                        soundCard = item.value,
                        audioCapturesDirectory = audioCapturesDirectory,
                        fileNameFontSize= fileNameFontSize,
                        onClick = { onSongButtonClicked(item.value) },
                        onPencilClicked = { newFileName ->
                            audioViewModel.renameSoundCard(item.value, newFileName, soundCardList)
                        },
                        onDeleteClick = {
                            audioViewModel.deleteSoundCard(item.value, soundCardList)
                        },
                        setShowOAuthWebView = { showOAuthWebView = it },
                        accessToken = accessToken.value,
                        context = LocalContext.current

                    )
                }
            }
        }

        ApiResponseDialog(audioViewModel)
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
    setShowOAuthWebView: (Boolean) -> Unit,
    accessToken: String?,
    context: Context
) {
    var showEditFileNameDialog by remember { mutableStateOf(false) }
    var showUploadDialog by remember { mutableStateOf(false) }

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

    if (showUploadDialog) {
        UploadSoundDialog(
            onDismiss = { showUploadDialog = false },
            onConfirm = { tags, description, license, pack, geotag ->
                if (accessToken != null) {
                    Log.d("filenamexd", audioCapturesDirectory.absolutePath + "/" + soundCard.fileName)
                    Log.d("accessToken", accessToken)
                    Log.d("License Info", "License: $license")
                    audioViewModel.uploadSound(
                        accessToken,
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
                    IconButton(onClick = { showEditFileNameDialog = true }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Title",
                            modifier = Modifier.size((fileNameFontSize * 1.5).toInt().dp)
                        )
                    }
                    IconButton(onClick = { onDeleteClick() }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size((fileNameFontSize * 1.5).toInt().dp)
                        )
                    }
                    IconButton(onClick = {
                        if (accessToken == null) {
                            setShowOAuthWebView(true)
                        } else {
                            showUploadDialog = true
                        }
                    }) {
                        Icon(Icons.Default.AddCircle,
                            contentDescription = "Upload",
                            modifier = Modifier.size((fileNameFontSize * 1.5).toInt().dp)
                        )
                    }

                }
            }

            Text(
                text = "Duration: ${audioViewModel.formatDuration(soundCard.duration.toLong())}",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = fileNameFontSize.sp
            )
            Text(
                text = "File Size: ${String.format("%.2f", soundCard.fileSize)} MB",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = fileNameFontSize.sp
            )
            Text(
                text = "Date: ${soundCard.date}",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = fileNameFontSize.sp
            )
        }
    }
}

@Composable
fun FileNameEditDialog(soundCard: SoundCard, onFileNameChange: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf(soundCard.fileName.dropLast(4)) }
    var showMaxLengthWarning by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit File Name") },
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
                    label = { Text("New File Name") }
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
                        onFileNameChange("$text.wav")
                    }
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
fun UploadSoundDialog(
    onDismiss: () -> Unit,
    onConfirm: (tags: String, description: String, license: String, pack: String, geotag: String) -> Unit
) {
    var tags by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var license by remember { mutableStateOf("") }
    var pack by remember { mutableStateOf("") }
    var geotag by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val licenseOptions = listOf("Attribution", "Attribution NonCommercial", "Creative Commons 0")
    val isUploadEnabled = tags.split(" ").filter { it.isNotEmpty() }.size >= 3 && description.isNotEmpty() && license.isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Upload Sound To FreeSound") },
        text = {
            Column {
                TextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags (Min 3 Required)") }
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Required)") }
                )
                Box {
                    TextField(
                        value = license,
                        onValueChange = { },
                        label = { Text("License (Required)") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        licenseOptions.forEach { option ->
                            DropdownMenuItem(text = { Text(option) },onClick = {
                                license = option
                                expanded = false
                            })
                        }
                    }
                }
//                TextField(
//                    value = pack,
//                    onValueChange = { pack = it },
//                    label = { Text("Pack (Optional)") }
//                )
//                TextField(
//                    value = geotag,
//                    onValueChange = { geotag = it },
//                    label = { Text("Geotag (Optional)") }
//                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(tags, description, license, pack, geotag)
                    onDismiss()
                },
                enabled = isUploadEnabled
            ) {
                Text("Upload")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


