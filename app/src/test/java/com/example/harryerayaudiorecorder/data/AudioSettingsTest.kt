package com.example.harryerayaudiorecorder.data
import android.media.AudioAttributes
import android.media.AudioFormat
import com.example.harryerayaudiorecorder.AudioSettings
import org.junit.Assert.assertEquals
import org.junit.Test

class AudioSettingsTest {

    @Test
    fun testDefaultValues() {
        val defaultSettings = AudioSettings()
        assertEquals(AudioFormat.ENCODING_PCM_16BIT, defaultSettings.encoding)
        assertEquals(44100, defaultSettings.sampleRate)
        assertEquals(AudioFormat.CHANNEL_IN_MONO, defaultSettings.channelMask)
        assertEquals(2 * 1024 * 1024, defaultSettings.bufferSize)
        assertEquals(listOf(AudioAttributes.USAGE_MEDIA, AudioAttributes.USAGE_GAME, AudioAttributes.USAGE_UNKNOWN), defaultSettings.usages)
    }

    @Test
    fun testCustomValues() {
        val customSettings = AudioSettings(
            encoding = AudioFormat.ENCODING_PCM_8BIT,
            sampleRate = 22050,
            channelMask = AudioFormat.CHANNEL_IN_STEREO,
            bufferSize = 1 * 1024 * 1024, // 1MB
            usages = listOf(AudioAttributes.USAGE_VOICE_COMMUNICATION)
        )
        assertEquals(AudioFormat.ENCODING_PCM_8BIT, customSettings.encoding)
        assertEquals(22050, customSettings.sampleRate)
        assertEquals(AudioFormat.CHANNEL_IN_STEREO, customSettings.channelMask)
        assertEquals(1 * 1024 * 1024, customSettings.bufferSize)
        assertEquals(listOf(AudioAttributes.USAGE_VOICE_COMMUNICATION), customSettings.usages)
    }

    @Test
    fun testCopyFunctionality() {
        val original = AudioSettings()
        val copied = original.copy(sampleRate = 48000)
        assertEquals(44100, original.sampleRate)
        assertEquals(48000, copied.sampleRate)
        assertEquals(original.encoding, copied.encoding)
        assertEquals(original.channelMask, copied.channelMask)
        assertEquals(original.bufferSize, copied.bufferSize)
        assertEquals(original.usages, copied.usages)
    }
}
