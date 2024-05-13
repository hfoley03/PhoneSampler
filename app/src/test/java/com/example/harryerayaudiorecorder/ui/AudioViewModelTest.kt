
import android.media.MediaPlayer
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.io.File

class AudioViewModelTest {

    @Mock
    private lateinit var mediaPlayerWrapper: MediaPlayerWrapper

    private lateinit var audioViewModel: AudioViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        audioViewModel = AudioViewModel(mediaPlayerWrapper)
    }

    @Test
    fun testPlayAudio() {
        val testFile = File("/harryerayaudiorecorder/resources/rooster.mp3")
        audioViewModel.playAudio(testFile)
        verify(mediaPlayerWrapper).setDataSource("/harryerayaudiorecorder/resources/rooster.mp3")
        verify(mediaPlayerWrapper).prepare()
        verify(mediaPlayerWrapper).start()
    }

    @Test
    fun testStopAudio() {
        audioViewModel.stopAudio()
        verify(mediaPlayerWrapper).stop()
        verify(mediaPlayerWrapper).release()
    }

    @Test
    fun testGetCurrentPosition() {
        `when`(mediaPlayerWrapper.getCurrentPosition()).thenReturn(100)
        assert(audioViewModel.getCurrentPosition() == 100)
        verify(mediaPlayerWrapper).getCurrentPosition()
    }

    @Test
    fun testGetAudioDuration() {
        val testFile = File("/harryerayaudiorecorder/resources/rooster.mp3")
        `when`(mediaPlayerWrapper.getDuration()).thenReturn(5000)
        audioViewModel.getAudioDuration(testFile)
        verify(mediaPlayerWrapper).setDataSource("/harryerayaudiorecorder/resources/rooster.mp3")
        verify(mediaPlayerWrapper).prepare()
        assert(audioViewModel.getAudioDuration(testFile) == 5000)
    }

    @Test
    fun testSeekTo() {
        audioViewModel.seekTo(1000)
        verify(mediaPlayerWrapper).seekTo(1000, MediaPlayer.SEEK_CLOSEST)
    }
//
//    @Test
//    fun testOnCleared() {
//        audioViewModel.onCleared()
//        verify(mediaPlayerWrapper).release()
//    }

//    @Test(expected = IOException::class)
//    fun testPlayAudioErrorHandling() {
//        val testFile = File("path/to/test/audio.mp3")
//        `when`(mediaPlayerWrapper.prepare()).thenThrow(IOException::class.java)
//        audioViewModel.playAudio(testFile)
//    }

}
