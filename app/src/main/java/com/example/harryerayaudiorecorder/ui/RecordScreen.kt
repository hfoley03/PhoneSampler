package com.example.harryerayaudiorecorder.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.harryerayaudiorecorder.MainActivity
import com.example.harryerayaudiorecorder.R

@Composable
fun RecordScreen(
    context: Context,
    onListButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        StopButton(onClick = {
            (context as MainActivity).stopRecorder()

        })
        RecordButton(onClick = {  (context as MainActivity).startRecorder() })
        IconButton(
            onClick = onListButtonClicked,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_menu),
                contentDescription = "List",
                tint = Color.Unspecified,
            )
        }
    }



}

@Preview(showBackground = true)
@Composable
fun previewRecordScreen(){
    RecordScreen(context = LocalContext.current, onListButtonClicked = { /*TODO*/ })
}

@Composable
fun RecordButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_record),
            contentDescription = "Record",
            tint = Color.Unspecified,
            )
    }
}

@Composable
fun StopButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_delete),
            contentDescription = "Stop Record",
            tint = Color.Unspecified
        )
    }
}

