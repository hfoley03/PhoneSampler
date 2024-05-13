package com.example.harryerayaudiorecorder

import android.media.AudioAttributes
import android.media.AudioFormat

data class AudioSettings(
    val encoding: Int = AudioFormat.ENCODING_PCM_16BIT,
    val sampleRate: Int = 44100,
    val channelMask: Int = AudioFormat.CHANNEL_IN_MONO,
    val bufferSize: Int = 2 * 1024 * 1024, // 2MB
    val usages: List<Int> = listOf(
        AudioAttributes.USAGE_MEDIA,
        AudioAttributes.USAGE_GAME,
        AudioAttributes.USAGE_UNKNOWN
    )
)
