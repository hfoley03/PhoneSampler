package com.example.harryerayaudiorecorder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.projection.MediaProjection
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class AudioRecordingTask(context: Context, mediaProjection: MediaProjection) : CoroutineScope {
    companion object {
        const val TAG = "AudioRecordingTask"
    }
    private var taskRunning: Boolean = false
    private var audioRecord: AudioRecord? = null
    private val job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    init {
        val audioSettings = AudioSettings();
        fun hasRecordAudioPermission(context: Context): Boolean {
            return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        }

        if(hasRecordAudioPermission(context))
        {
            //config the audioRecord
            val audioFormatSettings = AudioFormat.Builder().setEncoding(audioSettings.encoding).setSampleRate(audioSettings.sampleRate)
                .setChannelMask(audioSettings.channelMask).build()    //NEED TO CHANGE TO STEREO MIGHT EFFECT WAV CONVERSION ASK ERAY

            val audioPlaybackCapConfig = AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
                .addMatchingUsage(audioSettings.usages[0])
                .addMatchingUsage(audioSettings.usages[1])
                .addMatchingUsage(audioSettings.usages[2])
                .build()

            //build audioRecord object
            audioRecord = AudioRecord.Builder().setAudioFormat(audioFormatSettings).setBufferSizeInBytes(audioSettings.bufferSize) //2*1024*1024
                .setAudioPlaybackCaptureConfig(audioPlaybackCapConfig).build()

            Log.d(TAG, "audioRecord object started")

        } else {
            throw Exception("AudioPlaybackCapture: Permission Denied")
        }
    }



    fun cancel() {
        taskRunning = false
        job.cancel()
        audioRecord = null
        Log.d(TAG, "canceled")

    }

    fun execute(fileOutputStream: FileOutputStream) = launch {
        Log.d(TAG, "AudioRecord Start Recording")
        taskRunning = true
        audioRecord?.startRecording()
        withContext(Dispatchers.IO) {
            while (taskRunning) {
                try {
                    val tempByteArray = ByteArray(1024)
                    audioRecord?.read(tempByteArray, 0, tempByteArray.size) //read from Audio into the byteArray
                    fileOutputStream.write(tempByteArray)    //write the byteArray into the file
//                    Log.d(TAG, "File Written")
                } catch (e: IOException){
                    Log.d(TAG, e.toString())
                }
            }
        }
    }
}