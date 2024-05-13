package com.example.harryerayaudiorecorder

import android.media.AudioPlaybackCaptureConfiguration
import org.mockito.Mockito
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
@Implements(AudioPlaybackCaptureConfiguration.Builder::class)
class ShadowAudioPlaybackCaptureConfigurationBuilder {

    private val mockBuilder = Mockito.mock(AudioPlaybackCaptureConfiguration.Builder::class.java)

    init {
        // Configure the mock to return itself on addMatchingUsage to support fluent interfaces
        Mockito.`when`(mockBuilder.addMatchingUsage(Mockito.anyInt())).thenReturn(mockBuilder)
        Mockito.`when`(mockBuilder.build()).thenReturn(Mockito.mock(AudioPlaybackCaptureConfiguration::class.java))
    }

    @Implementation
    fun addMatchingUsage(usage: Int): AudioPlaybackCaptureConfiguration.Builder {
        return mockBuilder.addMatchingUsage(usage)
    }

    @Implementation
    fun build(): AudioPlaybackCaptureConfiguration {
        return mockBuilder.build()
    }
}