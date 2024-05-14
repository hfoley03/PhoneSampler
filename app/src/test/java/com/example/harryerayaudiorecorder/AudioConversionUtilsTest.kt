package com.example.harryerayaudiorecorder

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.io.*

@RunWith(MockitoJUnitRunner::class)
class AudioConversionUtilsTest {

    private lateinit var mockFileInput: File
    private lateinit var mockFileOutput: File
    private lateinit var byteArrayOutputStream: ByteArrayOutputStream
    private lateinit var byteArrayInputStream: ByteArrayInputStream
    private lateinit var dataOutputStream: DataOutputStream

    @Mock
    private lateinit var mockInputStream: FileInputStream
    @Mock
    private lateinit var mockOutputStream: FileOutputStream

    @Before
    fun setup() {
        mockFileInput = mock(File::class.java)
        mockFileOutput = mock(File::class.java)
        val exampleData = ByteArray(1024) { 0 }  // Example PCM data

        // Create actual stream instances and then spy on them
        byteArrayInputStream = spy(ByteArrayInputStream(exampleData))
        byteArrayOutputStream = spy(ByteArrayOutputStream())
        dataOutputStream = DataOutputStream(byteArrayOutputStream)

        // Setup File behavior
        `when`(mockFileInput.exists()).thenReturn(true)
        `when`(mockFileInput.canRead()).thenReturn(true)
        `when`(mockFileInput.length()).thenReturn(exampleData.size.toLong())
        `when`(mockFileInput.inputStream()).thenReturn(mockInputStream)
        `when`(mockFileOutput.outputStream()).thenReturn(mockOutputStream)
    }

    @Test
    fun testRawToWave() {
        // Call the method under test
        AudioConversionUtils.rawToWave(mockFileInput, mockFileOutput)

        // Verify that data was written to the output stream
        //assertThat(byteArrayOutputStream.size(), (equalTo(0)))
        verify(byteArrayOutputStream, atLeastOnce()).write(any(ByteArray::class.java), anyInt(), anyInt())
    }
}