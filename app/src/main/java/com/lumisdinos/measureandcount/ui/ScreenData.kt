package com.lumisdinos.measureandcount.ui

import androidx.compose.ui.graphics.Color
import com.lumisdinos.measureandcount.R
import com.lumisdinos.measureandcount.ui.model.ColorItem
import com.lumisdinos.measureandcount.ui.model.NewScreenType

val defaultScreenTypes = listOf(
    // Width x Length 12.5 x 54.0
    NewScreenType(columnNames = listOf(R.string.width_column, R.string.length_column)),
    // ↑Width x Length ↑12.5 x 54.0
    NewScreenType(directionColumn = 1, columnNames = listOf(R.string.width_column, R.string.length_column)),
    // Width x ↑Length 12.5 x ↑54.0
    NewScreenType(directionColumn = 2, columnNames = listOf(R.string.width_column, R.string.length_column)),
    // Width x Length x Color 12.5 x 54.0 x Blue
    NewScreenType(hasColor = true, columnNames = listOf(R.string.width_column, R.string.length_column)),
    // ↑Width x Length x Color ↑12.5 x 54.0 x Blue
    NewScreenType(true, 1, listOf(R.string.width_column, R.string.length_column)),
    // Width x ↑Length x Color 12.5 x ↑54.0 x Blue
    NewScreenType(true, 2, listOf(R.string.width_column, R.string.length_column)),
    // Width x Length x Height 12.5 x 54.0 x 10.0
    NewScreenType(columnNames = listOf(R.string.width_column, R.string.length_column, R.string.height_column)),
    // Length 54.0
    NewScreenType(columnNames = listOf(R.string.length_column)),
    // Length x Color 54.0 x Blue
    NewScreenType(hasColor = true, columnNames = listOf(R.string.length_column)),
    // Create own measure
    //NewScreenType() // empty, for custom
)

val colorList = listOf(
    ColorItem("White", Color.White),
    ColorItem("Black", Color.Black),
    ColorItem("Red", Color.Red),
    ColorItem("Green", Color.Green),
    ColorItem("Blue", Color.Blue),
    ColorItem("Yellow", Color.Yellow),
    ColorItem("Cyan", Color.Cyan),
    ColorItem("Magenta", Color.Magenta),
    ColorItem("Gray", Color.Gray),
    ColorItem("Dark Gray", Color.DarkGray),
    ColorItem("Light Gray", Color.LightGray),
    ColorItem("Orange", Color(0xFFFF9800)),
    ColorItem("Purple", Color(0xFF9C27B0)),
    ColorItem("Pink", Color(0xFFE91E63)),
    ColorItem("Brown", Color(0xFF795548)),
    ColorItem("Teal", Color(0xFF009688)),
    ColorItem("Lime", Color(0xFFCDDC39)),
    ColorItem("Indigo", Color(0xFF3F51B5)),
    ColorItem("Amber", Color(0xFFFFC107)),
    ColorItem("Deep Orange", Color(0xFFFF5722))
)

