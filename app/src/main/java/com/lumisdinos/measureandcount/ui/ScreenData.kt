package com.lumisdinos.measureandcount.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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


val colorListWithResId = listOf(
    ColorItem(R.string.color_white, Color.White.toArgb()),
    ColorItem(R.string.color_black, Color.Black.toArgb()),
    ColorItem(R.string.color_red, Color.Red.toArgb()),
    ColorItem(R.string.color_green, Color.Green.toArgb()),
    ColorItem(R.string.color_blue, Color.Blue.toArgb()),
    ColorItem(R.string.color_yellow, Color.Yellow.toArgb()),
    ColorItem(R.string.color_cyan, Color.Cyan.toArgb()),
    ColorItem(R.string.color_magenta, Color.Magenta.toArgb()),
    ColorItem(R.string.color_gray, Color.Gray.toArgb()),
    ColorItem(R.string.color_dark_gray, Color.DarkGray.toArgb()),
    ColorItem(R.string.color_light_gray, Color.LightGray.toArgb()),
    ColorItem(R.string.color_orange, Color(0xFFFF9800).toArgb()),
    ColorItem(R.string.color_purple, Color(0xFF9C27B0).toArgb()),
    ColorItem(R.string.color_pink, Color(0xFFE91E63).toArgb()),
    ColorItem(R.string.color_brown, Color(0xFF795548).toArgb()),
    ColorItem(R.string.color_teal, Color(0xFF009688).toArgb()),
    ColorItem(R.string.color_lime, Color(0xFFCDDC39).toArgb()),
    ColorItem(R.string.color_indigo, Color(0xFF3F51B5).toArgb()),
    ColorItem(R.string.color_amber, Color(0xFFFFC107).toArgb()),
    ColorItem(R.string.color_deep_orange, Color(0xFFFF5722).toArgb())
)

