package com.example.harryerayaudiorecorder.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.harryerayaudiorecorder.data.SoundCard

@Composable
fun RecordingsListScreen(
    onSongButtonClicked: (SoundCard) -> Unit,
    modifier: Modifier = Modifier
) {
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally) {
//        Button(
//            onClick = onSongButtonClicked,
//            modifier = Modifier.size(width = 80.dp, height = 80.dp)
//        ) {
//            Text(text = "Song")
//        }
//    }

    val sc1 = SoundCard("Recording 1", 01.30, "korhan_Yok.wav", 10.0)
    val sc2 = SoundCard("Recording 2", 00.31, "path/to/recording2", 2.0)
//    SoundRecordingCard(sc)
    val soundCards = listOf(
        sc1,sc2
    )
//    SoundRecordingPage(soundCards, { onSongButtonClicked(scList) })

    LazyColumn {
        items(count = soundCards.size) { index ->
            val item = soundCards[index]
            SoundRecordingCard(
                soundCard = item,
                onClick = {onSongButtonClicked(item)}
            )
        }
    }

}
//@Composable
//fun SoundRecordingPage(soundCards: List<SoundCard>, onSongButtonClicked: () -> Unit) {
//    LazyColumn {
//        items(count = soundCards.size) { index ->
//            val item = soundCards[index]
//            SoundRecordingCard(soundCard = item, onSongButtonClicked)
//        }
//
//    }
//}
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
            Text(text = "Duration: ${soundCard.duration}")
            Text(text = "File Size: ${soundCard.fileSize} MB")
        }
    }
}

