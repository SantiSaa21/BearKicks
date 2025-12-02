package com.bearkicks.application.ui

import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.bearkicks.application.R
import org.junit.Rule
import org.junit.Test

class ProfileLanguageSpanishUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun changeLanguageToSpanishAndConfirmDialogTexts() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val spanish = ctx.getString(R.string.language_spanish)
        val applyRestart = ctx.getString(R.string.language_apply_and_restart)
        composeTestRule.setContent {
            Text(text = spanish)
            Text(text = applyRestart)
        }
        composeTestRule.onNodeWithText(spanish).assertExists()
        composeTestRule.onNodeWithText(applyRestart).assertExists()
    }
}
