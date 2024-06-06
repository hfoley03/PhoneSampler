
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.example.harryerayaudiorecorder.PhoneSamplerAppBar
import com.example.harryerayaudiorecorder.PhoneSamplerScreen
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Config.OLDEST_SDK], manifest=Config.NONE)
class PhoneSamplerAppBarTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val mockNavigateUp = mockk<() -> Unit>()

    @Before
    fun setup() {
        // Mocking the navigateUp function
        every { mockNavigateUp.invoke() } answers { }
    }

    @Test
    fun phoneSamplerAppBar_displaysCorrectTitleAndBackButtonVisible() {
        composeTestRule.setContent {
            PhoneSamplerAppBar(
                currentScreen = PhoneSamplerScreen.Record,
                canNavigateBack = true,
                navigateUp = mockNavigateUp,
                fileNameFontSize = 22
            )
        }

        // Assertions to check if the title is set correctly and back button is visible
        composeTestRule.onNodeWithText("Record").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun phoneSamplerAppBar_noBackButtonWhenNavigationNotAllowed() {
        composeTestRule.setContent {
            PhoneSamplerAppBar(
                currentScreen = PhoneSamplerScreen.Record,
                canNavigateBack = false,
                navigateUp = mockNavigateUp,
                fileNameFontSize = 22
            )
        }

        // Assertions to check the back button is not visible when navigation is not allowed
        composeTestRule.onNodeWithContentDescription("Back").assertDoesNotExist()
    }
}
