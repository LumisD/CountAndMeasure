package com.lumisdinos.measureandcount.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UpArrowIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Filled.ArrowUpward,
        contentDescription = "Up",
        modifier = modifier
    )
}

@Composable
fun XIcon() {
    Icon(
        imageVector = Icons.Filled.Close,
        contentDescription = "X",
        modifier = Modifier.size(24.dp)
    )
}

@Composable
fun TextC(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = Color.Blue
        )
    )
}