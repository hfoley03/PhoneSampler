package com.example.harryerayaudiorecorder.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.harryerayaudiorecorder.data.SoundCard
import java.io.File

@Composable
fun RecordingsListScreen(
    onSongButtonClicked: (SoundCard) -> Unit,
    onThreeDotsClicked: (String) -> Unit,
    modifier: Modifier = Modifier
)  {
    val context = LocalContext.current
    val audioCapturesDirectory = File(context.getExternalFilesDir(null), "/AudioCaptures")

    val wavFiles = audioCapturesDirectory.listFiles { file ->
        file.isFile && file.name.lowercase().endsWith(".wav")
    }
//    wavFiles?.forEach { file ->
//        Log.d("wavfiles",file.name)
//    }
//    val soundCardList = mutableListOf<SoundCard>()
//
//    for (i in wavFiles.indices) {
//        val dur = AudioViewModel().getAudioDuration(wavFiles[i])
//        val fSizeMB = wavFiles[i].length().toDouble() / (1024 * 1024)
//        val sc = SoundCard( duration = dur, fileName = wavFiles[i].name, fileSize = fSizeMB)
//        soundCardList.add(sc)
//    }

    val soundCardList = wavFiles.map { file ->
        val dur = AudioViewModel().getAudioDuration(file)
        val fSizeMB = file.length().toDouble() / (1024 * 1024)
        mutableStateOf(
            SoundCard(
                duration = dur,
                fileName = file.name,
                fileSize = fSizeMB
            )
        )
    }



    LazyColumn {
        items(count = soundCardList.size) { index ->
            val item = soundCardList[index]
            SoundRecordingCard(
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

//@Composable
//fun SoundRecordingCard(soundCard: SoundCard,onClick: () -> Unit) {
//    Surface(
//        color = Color.White,
////        elevation = 4.dp,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//            .clickable(onClick = onClick)
//    ) {
//        // Your layout for sound recording card
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Text(text = soundCard.title)
//            Text(text = "Duration: ${AudioViewModel().formatDuration(soundCard.duration.toLong())}")
//            Text(text = "File Size: ${ String.format("%.2f", soundCard.fileSize)} MB")
//        }
//    }
//}

@Composable
fun SoundRecordingCard(
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
                AudioViewModel().renameFile(audioCapturesDirectory,
                    soundCard.fileName,
                    newFileName)
                onThreeDotsClicked(newFileName)
                showEditFileNameDialog = false

            }
        ) { showEditFileNameDialog = false }
    }
    Surface(
        color = Color.DarkGray,
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
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = { showEditFileNameDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Title")
                }
            }
            Text(text = "Duration: ${AudioViewModel().formatDuration(soundCard.duration.toLong())}")
            Text(text = "File Size: ${ String.format("%.2f", soundCard.fileSize)} MB")
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
