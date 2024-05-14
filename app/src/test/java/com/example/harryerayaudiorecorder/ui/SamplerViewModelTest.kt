
import com.example.harryerayaudiorecorder.data.SoundCard
import com.example.harryerayaudiorecorder.ui.SamplerViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SamplerViewModelTest {

    private lateinit var viewModel: SamplerViewModel

    @Before
    fun setUp() {
        viewModel = SamplerViewModel()
    }

    @Test
    fun `test setSoundCard updates uiState correctly`() = runBlockingTest {
        // Arrange
        val testSoundCard = SoundCard(duration = 300, fileName = "new_song.mp3", fileSize = 1.2, date = "2024-05-12")

        // Act
        viewModel.setSoundCard(testSoundCard)

        // Assert
        val currentState = viewModel.uiState.first()
        assertEquals(testSoundCard.duration, currentState.duration)
        assertEquals(testSoundCard.fileName, currentState.fileName)
        assertEquals(testSoundCard.fileSize, currentState.fileSize, 0.01)
        assertEquals(testSoundCard.date,"2024-05-12")
    }
}
