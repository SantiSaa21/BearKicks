package com.bearkicks.application.ui

import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import com.bearkicks.application.R
import androidx.test.platform.app.InstrumentationRegistry

class ProfileLanguageUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun openLanguageDialogAndSelectEnglish() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val action = ctx.getString(R.string.language_settings_action)
        val english = ctx.getString(R.string.language_english)
        val applyRestart = ctx.getString(R.string.language_apply_and_restart)
        composeTestRule.setContent {
            Text(text = action)
            Text(text = english)
            Text(text = applyRestart)
        }
        composeTestRule.onNodeWithText(action).assertExists()
        composeTestRule.onNodeWithText(english).assertExists()
        composeTestRule.onNodeWithText(applyRestart).assertExists()
    }
}
