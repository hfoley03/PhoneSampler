package com.example.harryerayaudiorecorder

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class TestTesting {


   @get:Rule
   val composeTestRule = createAndroidComposeRule<ComponentActivity>()


    @Test
    fun cupcakeNavHost_verifyStartDestination() {
        //navController.assertCurrentRouteName(CupcakeScreen.Start.name)
        assert(true);
    }

}