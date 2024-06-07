package com.example.harryerayaudiorecorder

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

    val outFile = File("/harryerayaudiorecorder/resources/")
    @Mock
    private lateinit var mockInputStream: FileInputStream
    @Mock
    private lateinit var mockOutputStream: FileOutputStream

//    @Before
//    fun setup() {
//        mockFileInput = mock(File::class.java)
//        mockFileOutput = mock(File::class.java)
//        val exampleData = ByteArray(1024) { 0 }  // Example PCM data
//
//        byteArrayInputStream = spy(ByteArrayInputStream(exampleData))
//        byteArrayOutputStream = spy(ByteArrayOutputStream())
//        dataOutputStream = DataOutputStream(byteArrayOutputStream)
//
//        `when`(mockFileInput.exists()).thenReturn(true)
//        `when`(mockFileInput.canRead()).thenReturn(true)
//        `when`(mockFileInput.length()).thenReturn(exampleData.size.toLong())
//        `when`(mockFileInput.inputStream()).thenReturn(mockInputStream)
//        `when`(mockFileOutput.outputStream()).thenReturn(mockOutputStream)
//    }

    @Test
    fun testRawToWave() {
        //val testFile = File("/harryerayaudiorecorder/resources/SystemAudio-07-06-2024-12-44-31.pcm")
        val testFile = File("/harryerayaudiorecorder/resources/rooster.mp3")

        AudioConversionUtils.rawToWave(testFile, outFile)
        verify(byteArrayOutputStream, atLeastOnce()).write(any(ByteArray::class.java), anyInt(), anyInt())
    }
}