package com.bearkicks.application.ui

import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.bearkicks.application.R
import org.junit.Rule
import org.junit.Test

class HomeLabelsUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeLabelsExist() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val offers = ctx.getString(R.string.home_offers)
        val news = ctx.getString(R.string.home_news)
        val shop = ctx.getString(R.string.nav_shop)
        composeTestRule.setContent {
            Text(text = offers)
            Text(text = news)
            Text(text = shop)
        }
        composeTestRule.onNodeWithText(offers).assertExists()
        composeTestRule.onNodeWithText(news).assertExists()
        composeTestRule.onNodeWithText(shop).assertExists()
    }
}
