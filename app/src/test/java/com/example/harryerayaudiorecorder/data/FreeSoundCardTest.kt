package com.example.harryerayaudiorecorder.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FreeSoundCardTest {

        @Test
        fun freesoundcardWithValidData() {
            val tags = listOf("ambient", "nature")
            val previews = mapOf("mp3" to "http://example.com/mp3", "ogg" to "http://example.com/ogg")
            val soundCard = FreesoundSoundCard(
                id = 12345,
                name = "rain",
                tags = tags,
                description = "rain sound",
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
            assertEquals("rain", soundCard.name)
            assertEquals(tags, soundCard.tags)
            assertEquals("rain sound", soundCard.description)
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
            assertEquals(4.5, soundCard.avgRating, 0.01)
            assertFalse(soundCard.isPlaying.value)
        }

    @Test
    fun freeSoundSoundCardEmptyandPreview() {
        val soundCard = FreesoundSoundCard(
            id = 12346,
            name = "Silent Sound",
            tags = emptyList(),
            description = "A sound with no tags or previews.",
            created = "2023-02-15",
            license = "Public Domain",
            channels = 1,
            filesize = 0,
            bitrate = 0,
            bitdepth = 0,
            duration = 0.0f,
            sampleRate = 0,
            username = "anonymous",
            download = "http://example.com/silent-download",
            previews = emptyMap(),
            avgRating = 0.0
        )
        assert(soundCard.tags.isEmpty())
        assert(soundCard.previews.isEmpty())
    }

    @Test
    fun freesoundSoundCardIsPlay() {
        val soundCard = FreesoundSoundCard(
            id = 12347,
            name = "Interactive Sound",
            tags = listOf("interactive"),
            description = "A sound that is interactive.",
            created = "2023-03-15",
            license = "MIT",
            channels = 1,
            filesize = 1024,
            bitrate = 128,
            bitdepth = 24,
            duration = 60.0f,
            sampleRate = 48000,
            username = "interactiveCreator",
            download = "http://example.com/interactive-download",
            previews = mapOf("mp3" to "http://example.com/interactive-mp3"),
            avgRating = 3.5
        )
        assertFalse(soundCard.isPlaying.value)
        soundCard.isPlaying.value = true
        assertTrue(soundCard.isPlaying.value)
    }
}