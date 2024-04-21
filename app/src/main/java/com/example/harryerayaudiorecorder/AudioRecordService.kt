package com.example.harryerayaudiorecorder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import android.util.Log
import androidx.activity.result.ActivityResult
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.*

class AudioRecordService : Service() {
    companion object {
        private lateinit var activityResult: ActivityResult
        const val TAG = "AudioRecordService"
        const val NOTIFICATION_ID = 19630303
        const val NOTIFICATION_CHANNEL_ID = "com.HarryErayAudioRecorder"
        const val NOTIFICATION_CHANNEL_NAME = "com.HarryErayAudioRecorder"

        fun start(context: Context, mediaProjectionActivityResult: ActivityResult) {
            activityResult = mediaProjectionActivityResult
            val intent = Intent(context, AudioRecordService::class.java)
            context.startForegroundService(intent)
        }
    }

    private val audioRecordingTask by lazy {
        val mediaProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val mediaProjection = mediaProjectionManager.getMediaProjection(
            activityResult.resultCode,
            activityResult.data!!
        )
        AudioRecordingTask(this, mediaProjection)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind")
        return null
    }

    private val timestamp = SimpleDateFormat("dd-MM-yyyy-hh-mm-ss", Locale.ITALY).format(Date())
    private val fileNamePCM = "SystemAudio-$timestamp.pcm" //PCM file

    private val fileOutputStream by lazy {
        val audioCapturesDirectory = File(getExternalFilesDir(null), "/AudioCaptures")
        if (!audioCapturesDirectory.exists()) {
            audioCapturesDirectory.mkdirs()
        }

        File(audioCapturesDirectory.absolutePath + "/" + fileNamePCM)
        FileOutputStream(File(audioCapturesDirectory.absolutePath + "/" + fileNamePCM))
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        //stop the task
        audioRecordingTask.cancel()
        Log.d(TAG, fileOutputStream.toString())
        val fileNameWAV = fileNamePCM.dropLast(3) + "wav"

        fileOutputStream.close()
        val audioCapturesDirectory = File(getExternalFilesDir(null), "/AudioCaptures")

        val f1 = File(audioCapturesDirectory.absolutePath + "/" + fileNamePCM) // The location of your PCM file

        val f2 =
            File(audioCapturesDirectory.absolutePath + "/" + fileNameWAV) // The location where you want your WAV file

        try {
            rawToWave(f1, f2)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")

        createNotification()
        startRecording()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification() {
        Log.d(TAG, "createNotification")

        createNotificationChannel(this)
        val notification = Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_record_voice_over_24)
            .setContentTitle(this.getString(R.string.app_name))
            .setContentText(this.getString(R.string.recording))
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setShowWhen(true)
            .build()
        val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            NOTIFICATION_ID,
            notification
        )
        startForeground(
            NOTIFICATION_ID,
            notification,
        )
    }

    private fun createNotificationChannel(context: Context) {
        Log.d(TAG, "createNotificationChannel")

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        Log.d(TAG, "createNotificationChannel finished")

    }

    private fun startRecording() {
        Log.d(TAG, "startRecording!!!")
        audioRecordingTask.execute(fileOutputStream)
    }

    @Throws(IOException::class)
    private fun rawToWave(rawFile: File, waveFile: File) {
        val rawData = ByteArray(rawFile.length().toInt())
        var input: DataInputStream? = null
        try {
            input = DataInputStream(FileInputStream(rawFile))
            input.read(rawData)
        } finally {
            input?.close()
        }
        var output: DataOutputStream? = null
        try {
            output = DataOutputStream(FileOutputStream(waveFile))
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF") // chunk id
            writeInt(output, 36 + rawData.size) // chunk size
            writeString(output, "WAVE") // format
            writeString(output, "fmt ") // subchunk 1 id
            writeInt(output, 16) // subchunk 1 size
            writeShort(output, 1.toShort()) // audio format (1 = PCM)
            writeShort(output, 1.toShort()) // number of channels
            writeInt(output, 44100) // sample rate
            writeInt(output, 44100 * 2) // byte rate
            writeShort(output, 2.toShort()) // block align
            writeShort(output, 16.toShort()) // bits per sample
            writeString(output, "data") // subchunk 2 id
            writeInt(output, rawData.size) // subchunk 2 size
            // Audio data (conversion big endian -> little endian)
            val shorts = ShortArray(rawData.size / 2)
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()[shorts]
            val bytes = ByteBuffer.allocate(shorts.size * 2)
            for (s in shorts) {
                bytes.putShort(s)
            }
            output.write(fullyReadFileToBytes(rawFile))
            Log.d(TAG, "to wav complete")
        } finally {
            output?.close()
        }
    }

    @Throws(IOException::class)
    fun fullyReadFileToBytes(f: File): ByteArray {
        val size = f.length().toInt()
        val bytes = ByteArray(size)
        val tmpBuff = ByteArray(size)
        val fis = FileInputStream(f)
        try {
            var read = fis.read(bytes, 0, size)
            if (read < size) {
                var remain = size - read
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain)
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read)
                    remain -= read
                }
            }
        } catch (e: IOException) {
            throw e
        } finally {
            fis.close()
        }
        return bytes
    }

    @Throws(IOException::class)
    private fun writeInt(output: DataOutputStream, value: Int) {
        output.write(value shr 0)
        output.write(value shr 8)
        output.write(value shr 16)
        output.write(value shr 24)
    }

    @Throws(IOException::class)
    private fun writeShort(output: DataOutputStream, value: Short) {
        output.write(value.toInt() shr 0)
        output.write(value.toInt() shr 8)
    }

    @Throws(IOException::class)
    private fun writeString(output: DataOutputStream, value: String) {
        for (i in 0 until value.length) {
            output.write(value[i].code)
        }
    }
}