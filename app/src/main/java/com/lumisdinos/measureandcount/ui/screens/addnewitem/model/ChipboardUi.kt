package com.lumisdinos.measureandcount.ui.screens.addnewitem.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.lumisdinos.measureandcount.data.db.model.Chipboard

data class ChipboardUi(
    val id: Int = 0,
    val unionId: Int = 0,
    val dimensions: Int = 1, // Number of dimensions, starts from 1
    val direction: Int = 0, // 0 - no direction, 1 to n - direction column
    val quantity: Int = 1,
    val colorName: String = "White",
    val color: Int = Color.White.toArgb(),
    val title1: String = "",
    val size1: Float = 0f,
    val title2: String = "",
    val size2: Float = 0f,
    val title3: String = "",
    val size3: Float = 0f,

    val quantityAsString: String = "1",
    val size1AsString: String = "",
    val size2AsString: String = "",
    val size3AsString: String = "",
    val chipboardAsString: String = ""
)

fun ChipboardUi.toChipboard(): Chipboard {
    return Chipboard(
        id = id,
        unionId = unionId,
        dimensions = dimensions,
        direction = direction,
        quantity = quantity,
        colorName = colorName,
        color = color,
        title1 = title1,
        size1 = size1,
        title2 = title2,
        size2 = size2,
        title3 = title3,
        size3 = size3
    )
}

fun Chipboard.toChipboardUi(): ChipboardUi {
    return ChipboardUi(
        id = id,
        unionId = unionId,
        dimensions = dimensions,
        colorName = colorName,
        color = color,
        direction = direction,
        quantity = quantity,
        title1 = title1,
        size1 = size1,
        title2 = title2,
        size2 = size2,
        title3 = title3,
        size3 = size3
    )
}