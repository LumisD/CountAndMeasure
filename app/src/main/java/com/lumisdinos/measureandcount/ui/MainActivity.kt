package com.lumisdinos.measureandcount.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.lumisdinos.measureandcount.ui.theme.MeasureAndCountTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            enableEdgeToEdge()
            MeasureAndCountTheme {
                AppNavigation()
            }
        }
    }

//    @Composable
//    private fun getStatusBarStyle(): SystemBarStyle = SystemBarStyle.run {
//        val color = Color(0xFFEFF7FF).toArgb()
//        if (isSystemInDarkTheme()) {
//            dark(color)
//        } else {
//            light(color, color)
//        }
//    }
}