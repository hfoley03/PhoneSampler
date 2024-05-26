package com.example.harryerayaudiorecorder.dataBase

import com.example.harryerayaudiorecorder.data.AudioRecordEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class AudioRecordEntityTest {

    @Test
    fun testAudioRecordEntityCreation() {
        val record = AudioRecordEntity(
            filename = "testFile.wav",
            filePath = "/path/to/testFile.wav",
            duration = 1000,
            fileSize = 1.2,
            date = "2023-05-26"
        )

        assertNotNull(record)
        assertEquals("testFile.wav", record.filename)
        assertEquals("/path/to/testFile.wav", record.filePath)
        assertEquals(1000, record.duration)
        assertEquals(1.2, record.fileSize, 0.0)
        assertEquals("2023-05-26", record.date)
    }

    @Test
    fun testDefaultValues() {
        val record = AudioRecordEntity(
            filename = "testFile.wav",
            filePath = "/path/to/testFile.wav"
        )

        assertEquals(0, record.duration)
        assertEquals(0.0, record.fileSize, 0.0)
        assertEquals("", record.date)
        assertEquals(0, record.id)
        assertFalse(record.isChecked)
    }

    @Test
    fun testPrimaryKeyAutoGeneration() {
        val record1 = AudioRecordEntity(
            filename = "testFile1.wav",
            filePath = "/path/to/testFile1.wav"
        )
        val record2 = AudioRecordEntity(
            filename = "testFile2.wav",
            filePath = "/path/to/testFile2.wav"
        )

        assertEquals(0, record1.id)
        assertEquals(0, record2.id)

        record1.id = 1
        record2.id = 2

        assertNotEquals(record1.id, record2.id)
        assertEquals(1, record1.id)
        assertEquals(2, record2.id)
    }

    @Test
    fun testUpdateEntity() {
        val record = AudioRecordEntity(
            filename = "testFile.wav",
            filePath = "/path/to/testFile.wav",
            duration = 1000,
            fileSize = 1.2,
            date = "2023-05-26"
        )

        val updatedRecord = record.copy(
            filename = "updatedTestFile.wav",
            filePath = "/new/path/to/testFile.wav",
            duration = 2000,
            fileSize = 2.4,
            date = "2024-05-26"
        )

        assertEquals("updatedTestFile.wav", updatedRecord.filename)
        assertEquals("/new/path/to/testFile.wav", updatedRecord.filePath)
        assertEquals(2000, updatedRecord.duration)
        assertEquals(2.4, updatedRecord.fileSize, 0.0)
        assertEquals("2024-05-26", updatedRecord.date)
    }
}