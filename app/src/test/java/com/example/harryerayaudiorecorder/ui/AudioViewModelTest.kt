package com.example.harryerayaudiorecorder.ui

import android.media.MediaPlayer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.IOException

class AudioViewModelTest {

    private lateinit var viewModel: AudioViewModel
    private val mediaPlayer: MediaPlayer = mockk(relaxed = true)
    private val file: File = mockk(relaxed = true)

    @Before
    fun setup() {
        viewModel = AudioViewModel()
        viewModel.mediaPlayer = mediaPlayer
    }

    @Test
    fun `test playAudio initializes and plays audio`() {
        every { mediaPlayer.prepare() } answers { nothing }
        every { file.absolutePath } returns "path/to/audio.mp3"

        viewModel.playAudio(file)

        verify { mediaPlayer.setDataSource("path/to/audio.mp3") }
        verify { mediaPlayer.prepare() }
        verify { mediaPlayer.start() }
    }

    @Test(expected = IOException::class)
    fun `test playAudio handles IOException`() {
        every { mediaPlayer.prepare() } throws IOException()

        viewModel.playAudio(file)
    }

    @Test
    fun `test stopAudio stops and releases mediaPlayer`() {
        viewModel.stopAudio()

        verify { mediaPlayer.stop() }
        verify { mediaPlayer.release() }
    }

    @Test
    fun `test onCleared releases mediaPlayer`() {
        viewModel.onCleared()

        verify { mediaPlayer.release() }
    }

    @Test
    fun `test getCurrentPosition returns correct position`() {
        every { mediaPlayer.isPlaying } returns true
        every { mediaPlayer.currentPosition } returns 1000

        val position = viewModel.getCurrentPosition()

        assertEquals(1000, position)
    }

    @Test
    fun `test getAudioDuration calculates duration`() {
        every { file.absolutePath } returns "path/to/file.mp3"
        every { mediaPlayer.prepare() } answers { nothing }
        every { mediaPlayer.duration } returns 120000

        val duration = viewModel.getAudioDuration(file)

        assertEquals(120000, duration)
    }

    @Test
    fun `test formatDuration formats correctly`() {
        val formatted = viewModel.formatDuration(3661000L) // 1 hour, 1 minute, 1 second

        assertEquals("01:01:01", formatted)
    }

    @Test
    fun `test seekTo seeks correctly`() {
        viewModel.seekTo(5000L)

        verify { mediaPlayer.seekTo(5000L, MediaPlayer.SEEK_CLOSEST) }
    }

    @Test
    fun `test renameFile renames file`() {
        val directory = mockk<File>(relaxed = true)
        every { directory.exists() } returns true
        every { file.exists() } returns true
        every { file.renameTo(any()) } returns true

        viewModel.renameFile(directory, "oldName.mp3", "newName.mp3")

        verify { file.renameTo(File(directory, "newName.mp3")) }
        }
}

