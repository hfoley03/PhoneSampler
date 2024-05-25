package com.example.harryerayaudiorecorder.ui

import AudioViewModel
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.harryerayaudiorecorder.data.SoundCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File



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

    //val soundCardList: MutableList<MutableState<SoundCard>> = mutableListOf()

// Launch a coroutine to fetch and populate the list
    LaunchedEffect(Unit) {        // Switch to the IO dispatcher for database operations
        withContext(Dispatchers.IO) {
            val soundRecordDatabaseList = audioViewModel.db.audioRecordDoa().getAll()

            // Map each record to a MutableState<SoundCard> and add it to the soundCardList
            soundRecordDatabaseList.forEach { record ->
                val soundCard = SoundCard(
                    duration = record.duration,
                    fileName = record.filename,
                    fileSize = record.fileSize,
                    date = record.date
                )
                Log.d("Created SoundCard", "${soundCard.fileName}") // Log each SoundCard

                // Add to the list on the main thread
                withContext(Dispatchers.Main) {
                    soundCardList.add(mutableStateOf(soundCard))
                }
            }
        }
    }




    LazyColumn(modifier = modifier) {
        items(soundCardList) { item ->
            SoundRecordingCard(
                audioViewModel,
                soundCard = item.value,
                audioCapturesDirectory = audioCapturesDirectory,
                onClick = { onSongButtonClicked(item.value) },
                onPencilClicked = { newFileName ->
                    audioViewModel.renameSoundCard(item.value, newFileName, soundCardList)
                },
                onDeleteClick = {
                    audioViewModel.deleteSoundCard(item.value, soundCardList) // Handle long click
                }
            )
        }
    }

}


//@Preview
//@Composable
//fun previewSoundRecordingCard(){
//    val audioCapturesDirectory = null
//
//    val mockFunction: (String) -> Unit = { input ->
//        println("Mock function received input: $input")}
//    val tempFile = createTempFile("mockFile", ".txt")
//
//    SoundRecordingCard(
//
//        soundCard = SoundCard(10, "Noise Recording 1", 16.0, "16-08-2022"),
//        audioCapturesDirectory = tempFile,
//        onClick = { /*TODO*/ },
//        onThreeDotsClicked = mockFunction
//    )

//}


@Composable
fun SoundRecordingCard(
    audioViewModel: AudioViewModel,
    soundCard: SoundCard,
    audioCapturesDirectory: File,
    onClick: () -> Unit,
    onPencilClicked: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    var showEditFileNameDialog by remember { mutableStateOf(false) }
    if (showEditFileNameDialog) {
        FileNameEditDialog(
            soundCard = soundCard,
            onFileNameChange = { newFileName ->
                audioViewModel.renameFileFromList(
                    soundCard.fileName,
                    newFileName)
                onPencilClicked(newFileName)
                showEditFileNameDialog = false

            }
        ) { showEditFileNameDialog = false }
    }
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
        shape = RoundedCornerShape(16.dp),

        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {  }, // Handle long press
                    onTap = { onClick() } // Handle single tap
                )
            }
    ) {
        // Your layout for sound recording card
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = soundCard.fileName,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    IconButton(onClick = { showEditFileNameDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Title")
                    }
                    IconButton(onClick = { onDeleteClick() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }

            }

            Text(text = "Duration: ${audioViewModel.formatDuration(soundCard.duration.toLong())}", color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(text = "File Size: ${ String.format("%.2f", soundCard.fileSize)} MB", color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(text = "Date: ${ soundCard.date }", color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
fun FileNameEditDialog(soundCard: SoundCard, onFileNameChange: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf(soundCard.fileName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit File Name") },
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
                    onFileNameChange(text)
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
