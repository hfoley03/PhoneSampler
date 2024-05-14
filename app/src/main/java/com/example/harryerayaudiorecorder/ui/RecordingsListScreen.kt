package com.example.harryerayaudiorecorder.ui

import AudioViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.harryerayaudiorecorder.data.SoundCard
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date



@Composable
fun RecordingsListScreen(
    audioViewModel: AudioViewModel,
    onSongButtonClicked: (SoundCard) -> Unit,
    onThreeDotsClicked: (String) -> Unit,
    modifier: Modifier = Modifier
)  {
    val context = LocalContext.current
    val audioCapturesDirectory = File(context.getExternalFilesDir(null), "/AudioCaptures")
//    var audioViewModel: AudioViewModel
//    audioViewModel = AudioViewModel(AndroidMediaPlayerWrapper())

    val wavFiles = audioCapturesDirectory.listFiles { file ->
        file.isFile && file.name.lowercase().endsWith(".wav")
    }


    val soundCardList = wavFiles.map { file ->
        val dur = audioViewModel.getAudioDuration(file)
        val fSizeMB = file.length().toDouble() / (1024 * 1024)
        val lastModDate = SimpleDateFormat("dd-MM-yyyy").format(Date(file.lastModified()))
        mutableStateOf(
            SoundCard(
                duration = dur,
                fileName = file.name,
                fileSize = fSizeMB,
                date = lastModDate.toString(),
            )
        )
    }



    LazyColumn {
        items(count = soundCardList.size) { index ->
            val item = soundCardList[index]
            SoundRecordingCard(
                audioViewModel,
                soundCard = item.value,
                audioCapturesDirectory = audioCapturesDirectory,
                onClick = {onSongButtonClicked(item.value)},
                onThreeDotsClicked = {newFileName ->
                    item.value = item.value.copy(fileName = newFileName)
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
    onThreeDotsClicked: (String) -> Unit
) {
    var showEditFileNameDialog by remember { mutableStateOf(false) }
    if (showEditFileNameDialog) {
        FileNameEditDialog(
            soundCard = soundCard,
            onFileNameChange = { newFileName ->
                audioViewModel.renameFileFromList(
                    soundCard.fileName,
                    newFileName)
                onThreeDotsClicked(newFileName)
                showEditFileNameDialog = false

            }
        ) { showEditFileNameDialog = false }
    }
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
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
                    color = MaterialTheme.colorScheme.inversePrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = { showEditFileNameDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Title")
                }
            }
            Text(text = "Duration: ${audioViewModel.formatDuration(soundCard.duration.toLong())}", color = MaterialTheme.colorScheme.onPrimary)
            Text(text = "File Size: ${ String.format("%.2f", soundCard.fileSize)} MB", color = MaterialTheme.colorScheme.onPrimary)
            Text(text = "Date: ${ soundCard.date }", color = MaterialTheme.colorScheme.onPrimary)
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
