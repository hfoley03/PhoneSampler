package com.example.harryerayaudiorecorder

import android.media.AudioFormat
import org.mockito.Mockito.mock
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

val mockAudioFormat = mock(AudioFormat::class.java)

@Implements(AudioFormat.Builder::class)
class ShadowAudioFormatBuilder {
    private var encoding = AudioFormat.ENCODING_DEFAULT
    private var sampleRate = 44100
    private var channelMask = AudioFormat.CHANNEL_IN_DEFAULT

    @Implementation
    fun setEncoding(encoding: Int): AudioFormat.Builder {
        this.encoding = encoding
        return AudioFormat.Builder() // Return a new instance of the actual Builder
    }

    @Implementation
    fun setSampleRate(sampleRate: Int): AudioFormat.Builder {
        this.sampleRate = sampleRate
        return AudioFormat.Builder() // Return a new instance of the actual Builder
    }

    @Implementation
    fun setChannelMask(channelMask: Int): AudioFormat.Builder {
        this.channelMask = channelMask
        return AudioFormat.Builder() // Return a new instance of the actual Builder
    }

    @Implementation
    fun build(): AudioFormat {
        // Return a new instance or a simple mocked object
        return mock(AudioFormat::class.java)
    }
}