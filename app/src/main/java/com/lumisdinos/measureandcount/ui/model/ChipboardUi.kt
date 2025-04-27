package com.lumisdinos.measureandcount.ui.model

import com.lumisdinos.measureandcount.data.db.model.Chipboard

data class ChipboardUi(
    val id: Int = 0,
    val unionId: Int = 0,
    val dimensions: Short = 1, // Number of dimensions, starts from 1
    val color: String = "White",
    val direction: Short = 0, // 0 - no direction, 1 to n - direction column
    val quantity: Short = 1,
    val title1: String = "",
    val size1: Float = 0f,
    val title2: String = "",
    val size2: Float = 0f,
    val title3: String = "",
    val size3: Float = 0f,
    val title4: String = "",
    val size4: Float = 0f,
    val title5: String = "",
    val size5: Float = 0f,
    val chipboardAsString: String = "",
)

fun ChipboardUi.toChipboard(): Chipboard {
    return Chipboard(
        id = id,
        unionId = unionId,
        dimensions = dimensions,
        color = color,
        direction = direction,
        quantity = quantity,
        title1 = title1,
        size1 = size1,
        title2 = title2,
        size2 = size2,
        title3 = title3,
        size3 = size3,
        title4 = title4,
        size4 = size4,
        title5 = title5,
        size5 = size5
    )
}