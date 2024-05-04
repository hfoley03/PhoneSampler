package com.example.harryerayaudiorecorder.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.harryerayaudiorecorder.data.SoundCard
import java.io.File

@Composable
fun RecordingsListScreen(
    onSongButtonClicked: (SoundCard) -> Unit,
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
    val soundCardList = mutableListOf<SoundCard>()

    for (i in wavFiles.indices) {
        val dur = AudioViewModel().getAudioDuration(wavFiles[i])
        val fSizeMB = wavFiles[i].length().toDouble() / (1024 * 1024)
        val sc = SoundCard(title = wavFiles[i].nameWithoutExtension, duration = dur, fileName = wavFiles[i].name, fileSize = fSizeMB)
        soundCardList.add(sc)
    }



    LazyColumn {
        items(count = soundCardList.size) { index ->
            val item = soundCardList[index]
            SoundRecordingCard(
                soundCard = item,
                onClick = {onSongButtonClicked(item)}
            )
        }
    }

}

@Composable
fun SoundRecordingCard(soundCard: SoundCard,onClick: () -> Unit) {
    Surface(
        color = Color.White,
//        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        // Your layout for sound recording card
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = soundCard.title)
            Text(text = "Duration: ${AudioViewModel().formatDuration(soundCard.duration.toLong())}")
            Text(text = "File Size: ${ String.format("%.2f", soundCard.fileSize)} MB")
        }
    }
}

