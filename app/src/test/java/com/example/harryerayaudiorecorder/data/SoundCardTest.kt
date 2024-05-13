package com.example.harryerayaudiorecorder.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class SoundCardTest {

    @Test
    fun testDefaultValues() {
        val soundCard = SoundCard()
        assertEquals(0, soundCard.duration)
        assertEquals("", soundCard.fileName)
        assertEquals(0.0, soundCard.fileSize, 0.0)
        assertEquals("", soundCard.date)
    }

    @Test
    fun testInitializedValues() {
        val soundCard = SoundCard(3600, "testFile.mp3", 3.5, "01-01-2020")
        assertEquals(3600, soundCard.duration)
        assertEquals("testFile.mp3", soundCard.fileName)
        assertEquals(3.5, soundCard.fileSize, 0.0)
        assertEquals("01-01-2020", soundCard.date)
    }

    @Test
    fun testEquality() {
        val soundCard1 = SoundCard(3600, "testFile.mp3", 3.5, "01-01-2020")
        val soundCard2 = SoundCard(3600, "testFile.mp3", 3.5, "01-01-2020")
        assertEquals(soundCard1, soundCard2)
    }

    @Test
    fun testInequality() {
        val soundCard1 = SoundCard(3600, "testFile.mp3", 3.5, "01-01-2020")
        val soundCard2 = SoundCard(3600, "testFile.mp3", 3.6, "01-01-2020")
        assertNotEquals(soundCard1, soundCard2)
    }

    @Test
    fun testCopy() {
        val soundCard1 = SoundCard(3600, "original.mp3", 3.5, "01-01-2020")
        val soundCard2 = soundCard1.copy(fileName = "copy.mp3")
        assertEquals(soundCard1.duration, soundCard2.duration)
        assertEquals("copy.mp3", soundCard2.fileName)
        assertEquals(soundCard1.fileSize, soundCard2.fileSize, 0.0)
        assertEquals(soundCard1.date, soundCard2.date)
        assertNotEquals(soundCard1.fileName, soundCard2.fileName)
    }
}
