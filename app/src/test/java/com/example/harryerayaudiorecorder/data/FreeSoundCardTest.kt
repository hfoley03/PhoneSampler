package com.example.harryerayaudiorecorder.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class FreeSoundCardTest {

        @Test
        fun freesoundcardWiothValidData() {
            val tags = listOf("ambient", "nature")
            val previews = mapOf("mp3" to "http://example.com/mp3", "ogg" to "http://example.com/ogg")
            val soundCard = FreesoundSoundCard(
                id = 12345,
                name = "Relaxing Nature Sound",
                tags = tags,
                description = "A soothing sound of nature to relax.",
                created = "2023-01-15",
                license = "Creative Commons",
                channels = 2,
                filesize = 10485760, // 10 MB
                bitrate = 192,
                bitdepth = 16,
                duration = 180.0f, // 3 minutes
                sampleRate = 44100,
                username = "soundCreator",
                download = "http://example.com/download",
                previews = previews,
                avgRating = 4.5
            )
            assertEquals(12345, soundCard.id)
            assertEquals("Relaxing Nature Sound", soundCard.name)
            assertEquals(tags, soundCard.tags)
            assertEquals("A soothing sound of nature to relax.", soundCard.description)
            assertEquals("2023-01-15", soundCard.created)
            assertEquals("Creative Commons", soundCard.license)
            assertEquals(2, soundCard.channels)
            assertEquals(10485760, soundCard.filesize)
            assertEquals(192, soundCard.bitrate)
            assertEquals(16, soundCard.bitdepth)
            assertEquals(180.0f, soundCard.duration)
            assertEquals(44100, soundCard.sampleRate)
            assertEquals("soundCreator", soundCard.username)
            assertEquals("http://example.com/download", soundCard.download)
            assertEquals(previews, soundCard.previews)
            assertEquals(4.5, soundCard.avgRating)
            assertFalse(soundCard.isPlaying.value)
        }
}