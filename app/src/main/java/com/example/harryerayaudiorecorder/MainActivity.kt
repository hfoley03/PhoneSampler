package com.example.harryerayaudiorecorder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.harryerayaudiorecorder.ui.theme.HarryErayAudioRecorderTheme
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview


class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private var recorderRunning by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        requestAudioPermissions()

        setContent {
            HarryErayAudioRecorderTheme {
//                RecordSwitchButton(applicationContext)
                SimpleFrontPage(applicationContext)
            }
        }

    }

    @Composable
    fun RecordSwitchButton(context: Context) {
        val mycontext = context
        Button(
            onClick = {
                if (recorderRunning) stopRecorder() else startRecorder()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = if (recorderRunning) "Stop Recording" else "Start Recording")
        }
    }

    @Preview
    @Composable
    private fun SimpleFrontPagePreview(){
        SimpleFrontPage(context = this)
    }

    @Composable
    fun SimpleFrontPage(context: Context){
        Column (modifier = Modifier.fillMaxSize(),
            verticalArrangement =  Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RecordSwitchButton(context)

            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Play Recording")
            }
        }
    }

    private fun startRecorder(){
        Log.d(TAG, "startRecorder")

        val mediaProjectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        resultLauncher.launch(mediaProjectionManager.createScreenCaptureIntent())
    }

    private fun stopRecorder() {
        Log.d(TAG, "stopRecorder")

        switchButtonStyle(false)
        val intent = Intent(this, AudioRecordService::class.java)
        stopService(intent)
    }

    private fun switchButtonStyle(boolean: Boolean){
        Log.d(TAG, "switchButtonStyle")
        recorderRunning = boolean
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d(TAG, "Media projection permission granted")
            switchButtonStyle(true)
            AudioRecordService.start(this.applicationContext, it)
        }

    private fun requestAudioPermissions() {
        Log.d(TAG, "requestAudioPermissions")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }
    }
}
