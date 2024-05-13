package com.example.harryerayaudiorecorder
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager

class MediaProjectionManagerWrapper(private val context: Context) {
    private val mediaProjectionManager: MediaProjectionManager by lazy {
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    fun getMediaProjection(resultCode: Int, data: Intent?): MediaProjection? {
        return if (data == null) {
            null
        } else {
            mediaProjectionManager.getMediaProjection(resultCode, data)
        }
    }
}
