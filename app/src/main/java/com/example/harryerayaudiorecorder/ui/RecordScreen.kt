package com.example.harryerayaudiorecorder.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
        .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        StopButton(context, onClick = { (context as MainActivity).stopRecorder() })
        RecordButton(onClick = {  (context as MainActivity).startRecorder() })
        IconButton(
            onClick = onListButtonClicked,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_menu),
                contentDescription = "List",
                tint = MaterialTheme.colorScheme.onBackground,
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
    context: Context,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        ((context as MainActivity).recorderRunning)
    ) {

        if ((context as MainActivity).recorderRunning){
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_delete_disabled),
                contentDescription = "Stop Record",
                tint = MaterialTheme.colorScheme.onBackground
            )
        } else {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_delete),
                contentDescription = "Recorder is not running",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BottomSheet(onDismiss: () -> Unit) {
//    val modalBottomSheetState = rememberModalBottomSheetState()
//    var text by remember { mutableStateOf("") }
//
//    ModalBottomSheet(
//        onDismissRequest = { onDismiss() },
//        sheetState = modalBottomSheetState,
//        dragHandle = { BottomSheetDefaults.DragHandle() },
//    ) {
//        Column (
//            modifier = Modifier.fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ){
//
//
//            Text(text = "Save Recording?")
//            OutlinedTextField(
//                value = text,
//                onValueChange = { text = it },
//                label = { Text("Input File Name") }
//            )
//            Row(
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Button(onClick = { /*TODO*/ }) {
//                    Text(text = "Cancel")
//                }
//                Button(onClick = { /*TODO*/ }) {
//                    Text(text = "Save")
//                }
//            }
//        }
//    }
//}

@Composable
fun BottomSheet(onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("File Name") },
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
//                    onFileNameChange(text)
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


