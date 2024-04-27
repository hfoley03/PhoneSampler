package com.example.harryerayaudiorecorder.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RecordScreen(
    onListButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(modifier = Modifier.fillMaxSize().background(Color.Black),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.End) {
        Button(
            onClick = onListButtonClicked,
            modifier = Modifier.size(width = 80.dp, height = 80.dp)
        ) {
            Text(text = "List")
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            onClick = { },
            modifier = Modifier.size(width = 80.dp, height = 80.dp)
        ) {
//            Text(text = "Rec")
        }
    }
}

@Composable
fun ListButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = { },
            modifier = Modifier.size(width = 80.dp, height = 80.dp)
        ) {
            Text(text = "Lists")
        }
    }
}