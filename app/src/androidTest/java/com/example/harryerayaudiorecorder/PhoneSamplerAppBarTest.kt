package com.example.harryerayaudiorecorder

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify

class PhoneSamplerAppBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navigateUpMock: () -> Unit

    @Before
    fun setup() {
        navigateUpMock
//        val navController = TestNavHostController

    }

    @Test
    fun phoneSamplerAppBar_ShouldDisplayCorrectTitle() {
        composeTestRule.setContent {
            MaterialTheme {
                PhoneSamplerAppBar(
                    currentScreen = PhoneSamplerScreen.Record,
                    canNavigateBack = false,
                    navigateUp = navigateUpMock
                )
            }
        }

        composeTestRule.onNodeWithText("Record").assertIsDisplayed()
    }

    @Test
    fun phoneSamplerAppBar_ShouldShowBackButton_WhenCanNavigateBackIsTrue() {
        composeTestRule.setContent {
            MaterialTheme {
                PhoneSamplerAppBar(
                    currentScreen = PhoneSamplerScreen.Record,
                    canNavigateBack = true,
                    navigateUp = navigateUpMock
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun phoneSamplerAppBar_ShouldNotShowBackButton_WhenCanNavigateBackIsFalse() {
        composeTestRule.setContent {
            MaterialTheme {
                PhoneSamplerAppBar(
                    currentScreen = PhoneSamplerScreen.Record,
                    canNavigateBack = false,
                    navigateUp = navigateUpMock
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Back").assertDoesNotExist()
    }

    @Test
    fun phoneSamplerAppBar_ShouldInvokeNavigateUp_WhenBackButtonClicked() {
        composeTestRule.setContent {
            MaterialTheme {
                PhoneSamplerAppBar(
                    currentScreen = PhoneSamplerScreen.Record,
                    canNavigateBack = true,
                    navigateUp = navigateUpMock
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        verify(navigateUpMock).invoke()
    }

//    private fun performNavigateUp() {
//        val backText = composeTestRule.activity.getString(R.string.back_button)
//        composeTestRule.onNodeWithContentDescription(backText).performClick()
//    }
}
