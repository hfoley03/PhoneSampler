
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.harryerayaudiorecorder.MainActivity
import com.example.harryerayaudiorecorder.PhoneSamplerApp
import com.example.harryerayaudiorecorder.PhoneSamplerScreen
import com.example.harryerayaudiorecorder.ui.SamplerViewModel
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class AppIntegrationTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var navController: TestNavHostController
    private lateinit var viewModel: SamplerViewModel
    private lateinit var audioViewModel: AudioViewModel
    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun testNavigationFromRecordToPlayback() = runBlockingTest {
        // Initialize the TestNavHostController for navigation testing
        navController = TestNavHostController(composeTestRule.activity)
        // Use Mockito to create mocks
        viewModel = Mockito.mock(SamplerViewModel::class.java)
        audioViewModel = Mockito.mock(AudioViewModel::class.java)

        composeTestRule.setContent {
            PhoneSamplerApp(viewModel, navController, audioViewModel)
        }

        // Simulate interactions and verify navigation
        composeTestRule.onNodeWithText("Record").performClick()
        composeTestRule.onNodeWithText("Recordings List").performClick()
        assert(navController.currentDestination?.route == PhoneSamplerScreen.RecordingsList.name)

        composeTestRule.onNodeWithText("Playback").performClick()
        assert(navController.currentDestination?.route == PhoneSamplerScreen.Playback.name)
    }
}
