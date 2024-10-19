package com.example.lab2

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertValueEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.lab2.ui.CanvasScreen


import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNavigationToCanvasScreen() {
        // Launch the Composable
        composeTestRule.setContent {
            ComposeNavigation()
        }
        // Find the button and perform a click action
        composeTestRule.onNodeWithText("Create new canvas")
            .assertIsDisplayed()
            .performClick()
    }


    @Test
    fun testBrushShapeChangesOnClick() {
        // Step 1: Set up CanvasScreen in the test environment
        composeTestRule.setContent {
            ComposeNavigation()
        }

        // Navigate to the CanvasScreen
        composeTestRule.onNodeWithText("Create new canvas").performClick()
        // Verify that the initial brush shape is "Round"
        composeTestRule.onNodeWithText("Round Brush").assertIsDisplayed()
        // Click the "Square Brush" button to change the shape
        composeTestRule.onNodeWithText("Square Brush").performClick()
        // Verify that the brush shape has changed to Square in the UI
        composeTestRule.onNodeWithText("Square Brush").assertExists()
    }


    @Test
    fun testBrushColorChangesOnClick() {
        // Step 1: Set up CanvasScreen in the test environment
        composeTestRule.setContent {
            ComposeNavigation()
        }

        // Navigate to the CanvasScreen
        composeTestRule.onNodeWithText("Create new canvas").performClick()
        composeTestRule.onNodeWithContentDescription("Black Color").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Red Color").performClick()
        composeTestRule.onNodeWithContentDescription("Red Color").assertExists()
    }


//    @Test
//    fun testBrushSizeChangesWithSlider() {
//        // Step 1: Set up CanvasScreen in the test environment
//        composeTestRule.setContent {
//            ComposeNavigation()
//        }
//
//        composeTestRule.onNodeWithText("Create new canvas").performClick()
//
//        // Verify that the slider for brush size is displayed
//        composeTestRule.onNodeWithContentDescription("Brush Size Slider").assertIsDisplayed()
//        // Simulate a slider drag to increase the brush size
//        composeTestRule.onNodeWithContentDescription("Brush Size Slider").performSemanticsAction(
//            SemanticsActions.SetProgress
//        ) {
//            it(50f)
//        }
//
//        composeTestRule.onNodeWithContentDescription("Brush Size Slider")
//            .assertValueEquals("Brush size: 50")
//    }
}





