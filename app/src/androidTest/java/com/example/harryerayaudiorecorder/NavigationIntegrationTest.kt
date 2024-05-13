package com.example.harryerayaudiorecorder

import AndroidMediaPlayerWrapper
import AudioViewModel
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.example.harryerayaudiorecorder.ui.SamplerViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationIntegrationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: TestNavHostController

    @Before
    fun setupCupcakeNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            PhoneSamplerApp(
                    context = navController.context,
                    viewModel = SamplerViewModel(),
                    navController = navController,
                    audioViewModel = AudioViewModel(AndroidMediaPlayerWrapper())
                )
        }
    }

    @Test
    fun cupcakeNavHost_verifyStartDestination() {
        //navController.assertCurrentRouteName(PhoneSamplerScreen.Start.name)
        composeTestRule.onNodeWithText("List Recordings").performClick()

    }
//    @get:Rule
//    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
//
//    @Test
//    fun navigateThroughApp() {
//        activityScenarioRule.scenario.onActivity { activity ->
//            activity.setContent {
//                val navController = rememberNavController()
//                PhoneSamplerApp(
//                    context = activity,
//                    viewModel = SamplerViewModel(),
//                    navController = navController,
//                    audioViewModel = AudioViewModel(AndroidMediaPlayerWrapper())
//                )
//            }
//
//            // Use Compose Test Rule linked to the activity scenario
//            val composeTestRule = createAndroidComposeRule<MainActivity>()
//            composeTestRule.setContent {
//                PhoneSamplerApp(
//                    context = composeTestRule.activity,
//                    viewModel = SamplerViewModel(),
//                    navController = rememberNavController(),
//                    audioViewModel = AudioViewModel(AndroidMediaPlayerWrapper())
//                )
//            }

            // Navigation tests...
//            composeTestRule.onNodeWithText("List Recordings").performClick()
//            composeTestRule.onNodeWithText("Recordings List").assertIsDisplayed()
//            composeTestRule.onNodeWithText("Play Song").performClick()
//            composeTestRule.onNodeWithText("Playback").assertIsDisplayed()
//            composeTestRule.onNodeWithContentDescription("Back").performClick()
//            composeTestRule.onNodeWithText("Recordings List").assertIsDisplayed()
//        }
//    }
}