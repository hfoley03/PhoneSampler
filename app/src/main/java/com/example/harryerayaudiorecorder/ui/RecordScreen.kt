package com.example.harryerayaudiorecorder.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(onDismiss: () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    var text by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){


            Text(text = "Save Recording?")
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Input File Name") }
            )
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Cancel")
                }
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Save")
                }
            }
        }
    }
}
