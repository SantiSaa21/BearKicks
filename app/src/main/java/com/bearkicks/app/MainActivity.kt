package com.bearkicks.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.bearkicks.app.navigation.AppNavigation
import com.bearkicks.app.ui.theme.BearKicksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BearKicksTheme {
                AppNavigation()
            }
        }
    }
}
