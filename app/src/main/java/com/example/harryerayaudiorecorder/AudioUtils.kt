package com.example.harryerayaudiorecorder

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object AudioUtils {
    fun hasRecordAudioPermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
}
