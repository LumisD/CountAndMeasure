package com.lumisdinos.measureandcount.ui.screens.count.model

import com.lumisdinos.measureandcount.data.db.model.Chipboard

data class ChipboardUi(
    val id: Int = 0,
    val unionId: Int = 0,
    val state: Int = 0,//0 - not found, 1 - found, 2 - unknown
    val dimensions: Short = 1, // Number of dimensions, starts from 1
    val direction: Short = 0, // 0 - no direction, 1 to n - direction column
    val quantity: Short = 1,
    val colorName: String = "",
    val color: Int = 0,
    val title1: String = "",
    val size1: Float = 0f,
    val difference1: Float = 0f,//diff between real measured size and size1
    val title2: String = "",
    val size2: Float = 0f,
    val difference2: Float = 0f,
    val title3: String = "",
    val size3: Float = 0f,
    val difference3: Float = 0f,

    val quantityAsString: String = "1",
    val size1AsString: String = "",
    val diff1AsString: String = "",
    val size2AsString: String = "",
    val diff2AsString: String = "",
    val size3AsString: String = "",
    val diff3AsString: String = "",
    val chipboardAsString: String = "",
    val allDiffsAsString: String = "",

    val isUnderReview: Boolean = false
)

fun ChipboardUi.toChipboard(): Chipboard {
    return Chipboard(
        id = id,
        unionId = unionId,
        state = state,
        dimensions = dimensions,
        direction = direction,
        quantity = quantity,
        colorName = colorName,
        color = color,
        title1 = title1,
        size1 = size1,
        difference1 = difference1,
        title2 = title2,
        size2 = size2,
        difference2 = difference2,
        title3 = title3,
        size3 = size3,
        difference3 = difference3
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
        difference1 = difference1,
        title2 = title2,
        size2 = size2,
        difference2 = difference2,
        title3 = title3,
        size3 = size3,
        difference3 = difference3

    )
}