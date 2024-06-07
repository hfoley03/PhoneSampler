package com.example.harryerayaudiorecorder.ui

import AudioViewModel
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.harryerayaudiorecorder.ApiResponseDialog
import com.example.harryerayaudiorecorder.R
import com.example.harryerayaudiorecorder.data.FreesoundSoundCard
import com.example.harryerayaudiorecorder.data.SoundCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecordingsListScreen(
    audioViewModel: AudioViewModel,
    onSongButtonClicked: (SoundCard) -> Unit,
    searchText: String,
    modifier: Modifier = Modifier
)  {
    val context = LocalContext.current
    val audioCapturesDirectory = File(context.getExternalFilesDir(null), "/AudioCaptures")
    val soundCardList = remember { mutableStateListOf<MutableState<SoundCard>>() }
    val fileNameFontSize = when {
        SamplerViewModel().isTablet() -> 32
        else -> 16
    }
    val accessToken = remember { mutableStateOf<String?>(null) }
    var fsSoundCards = remember { mutableStateListOf<MutableState<FreesoundSoundCard>>() }
    var fileOpacity by rememberSaveable { mutableStateOf(0.75f) }
    var downloadTrigger by rememberSaveable { mutableStateOf(false) }
    val sortOptions = listOf("Name","Duration","Size", "Date")
    var sortBy by rememberSaveable { mutableStateOf("Name") }
    var expanded by rememberSaveable { mutableStateOf(false) }


    LaunchedEffect(Unit,downloadTrigger) {
        soundCardList.clear()
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

        // Add a dummy sound card
        val dummySoundCard = SoundCard(
            duration = 20,
            fileName = "Welcome!.wav",
            fileSize = 1.7,
            date = "01-01-2024"
        )
        withContext(Dispatchers.Main) {
            soundCardList.add(mutableStateOf(dummySoundCard))
        }

        accessToken.value = audioViewModel.getAccessToken(context)

    }

    val filteredSoundCards = if (searchText.isBlank()) {
        sortSoundCards(soundCardList, sortBy)
    } else {
        sortSoundCards(soundCardList.filter {
            it.value.fileName.contains(searchText, ignoreCase = true)
        }, sortBy)
    }




    LaunchedEffect(searchText, fileOpacity) {
        // this (.25f) is the web search mode
        if (fileOpacity == 0.25f && searchText.isNotEmpty()) {
            audioViewModel.performSearchWithCoroutines(
                clientSecret = "DFYwiCdqrNbhB9RFGiENSXURVlF30uGFrGcLMFWy",
                searchText = searchText,
                updateUI = { newSounds ->
                    fsSoundCards.clear()
                    fsSoundCards.addAll(newSounds)
                }
            )
        }
    }

    val filteredFsSoundCards = if (searchText.isBlank()) {
        sortFsSoundCards(fsSoundCards, sortBy)
    } else {
        sortFsSoundCards(fsSoundCards.filter {
            it.value.name.contains(searchText, ignoreCase = true)
        }, sortBy)
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = fileNameFontSize.dp / 2f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clickable(onClick = {
                        fileOpacity = 0.75f
                    })
                    .weight(1f)
                    .padding((fileNameFontSize / 2).dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = fileOpacity),
                        shape = RoundedCornerShape((fileNameFontSize).dp)
                    )   ,
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "My Files",
                        fontSize = fileNameFontSize.sp,
                        modifier = Modifier.padding(fileNameFontSize.dp/2)
                    )
                }
            }

            // Spacer for visual separation
//            Spacer(modifier = Modifier.width(fileNameFontSize.dp))

            // Box for "Web" with text first and icon second
            Box(
                modifier = Modifier
                    .clickable(onClick = {
                        fileOpacity = 0.25f
                    })
                    .weight(1f)
                    .padding((fileNameFontSize / 2).dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 1.0f - fileOpacity),
                        shape = RoundedCornerShape((fileNameFontSize).dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "FreeSound",
                        fontSize = fileNameFontSize.sp,
                        modifier = Modifier.padding(fileNameFontSize.dp/2)
                    )
                }
            }
            Box(
                modifier = Modifier

                    .weight(0.4f)
                    .padding((fileNameFontSize / 2).dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape((fileNameFontSize).dp)
                    ),
                contentAlignment = Alignment.Center
            )
            {
                IconButton(onClick = { expanded = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.round_sort_24),
                                contentDescription = "Dropdown")
                        }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },

                ) {
                    sortOptions.forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = {
                            sortBy = option
                            expanded = false
                        })
                    }
                }
            }
        }
        // means web button selected
        if (fileOpacity == 0.25f) {
            LazyColumn(modifier = Modifier.padding(top = (fileNameFontSize/4).dp)) {
                items(filteredFsSoundCards) { item ->
                    FsSoundCard(item,
                        fileNameFontSize,
                        audioViewModel,
                        audioCapturesDirectory,
                        downloadTrigger = downloadTrigger
                    ) { downloadTrigger = it }
                }
            }
        }
        else if(fileOpacity == 0.75f) {
            LazyColumn(modifier = Modifier.padding(top = (fileNameFontSize/4).dp)) {
                items(filteredSoundCards) { item ->
                    SoundRecordingCard(

                        audioViewModel,
                        soundCard = item.value,
                        audioCapturesDirectory = audioCapturesDirectory,
                        fileNameFontSize = fileNameFontSize,
                        onClick = { onSongButtonClicked(item.value) },
                        onPencilClicked = { newFileName ->
                            audioViewModel.renameSoundCard(
                                item.value,
                                newFileName,
                                soundCardList
                            )
                        },
                        onDeleteClick = {
                            audioViewModel.deleteSoundCard(item.value, soundCardList)
                        },
                        context = LocalContext.current

                    )
                }
            }
        }
    }
    ApiResponseDialog(audioViewModel)
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
        },
        modifier = Modifier.testTag("FileNameEditDialog")
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
        },
        modifier = Modifier.testTag("UploadDialog")
    )
}


fun sortSoundCards(cards: List<MutableState<SoundCard>>, sortBy: String): List<MutableState<SoundCard>> {
    return when (sortBy) {
        "Name" -> cards.sortedBy { it.value.fileName }
        "Duration" -> cards.sortedBy { it.value.duration }
        "Size" -> cards.sortedBy { it.value.fileSize }
        "Date" -> cards.sortedBy { it.value.date }
        else -> cards
    }
}

fun sortFsSoundCards(cards: List<MutableState<FreesoundSoundCard>>, sortBy: String): List<MutableState<FreesoundSoundCard>> {
    return when (sortBy) {
        "Name" -> cards.sortedBy { it.value.name }
        "Duration" -> cards.sortedBy { it.value.duration }
        "Size" -> cards.sortedBy { it.value.filesize }
        "Date" -> cards.sortedBy { it.value.created }
        else -> cards
    }
}