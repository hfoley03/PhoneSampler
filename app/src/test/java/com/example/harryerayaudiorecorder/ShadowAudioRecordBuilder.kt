package com.example.harryerayaudiorecorder

import android.media.AudioFormat
import android.media.AudioRecord
import org.mockito.Mockito
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@Implements(AudioRecord.Builder::class)
class ShadowAudioRecordBuilder {

    private val mockBuilder = Mockito.mock(AudioRecord.Builder::class.java)
    private val mockAudioRecord = Mockito.mock(AudioRecord::class.java)

    init {
        // Configure the mock to return itself on setAudioFormat to support fluent interfaces
        Mockito.`when`(mockBuilder.setAudioFormat(Mockito.any(AudioFormat::class.java))).thenReturn(mockBuilder)
        Mockito.`when`(mockBuilder.setBufferSizeInBytes(Mockito.anyInt())).thenReturn(mockBuilder)
        Mockito.`when`(mockBuilder.build()).thenReturn(mockAudioRecord)
    }

    @Implementation
    fun setAudioFormat(audioFormat: AudioFormat): AudioRecord.Builder {
        return mockBuilder.setAudioFormat(audioFormat)
    }

    @Implementation
    fun setBufferSizeInBytes(size: Int): AudioRecord.Builder {
        return mockBuilder.setBufferSizeInBytes(size)
    }

    @Implementation
    fun build(): AudioRecord {
        //return mockBuilder.build()
        return mockAudioRecord
    }
}
