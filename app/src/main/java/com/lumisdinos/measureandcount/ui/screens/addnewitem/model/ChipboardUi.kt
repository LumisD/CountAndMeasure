package com.lumisdinos.measureandcount.ui.screens.addnewitem.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.lumisdinos.measureandcount.data.db.model.Chipboard
import com.lumisdinos.measureandcount.ui.model.Shareable
import com.lumisdinos.measureandcount.ui.model.UnionOfChipboardsUI

data class ChipboardUi(
    val id: Int = 0,
    val unionId: Int = 0,
    val quantity: Int = 1,
    val colorName: String = "White",
    val color: Int = Color.White.toArgb(),
    val size1: Float = 0f,
    val size2: Float = 0f,
    val size3: Float = 0f,

    val quantityAsString: String = "1",
    val size1AsString: String = "",
    val size2AsString: String = "",
    val size3AsString: String = "",
    val chipboardAsString: String = ""
) : Shareable {

    override fun getShareableString(
        union: UnionOfChipboardsUI,
        notFoundText: String,
        foundText: String,
        realSizeText: String
    ): String {
        val builder = StringBuilder()
        val dimensions = union.dimensions
        val direction = union.direction

        for (i in 1..dimensions) {
            if (direction == i) {
                builder.append("↑")
            }
            when (i) {
                1 -> builder.append(size1)
                2 -> builder.append(size2)
                3 -> builder.append(size3)
            }
            if (i < dimensions) {
                builder.append(" x ")
            }
        }
        builder.append(" - $quantity")
        if (union.hasColor && colorName.isNotBlank()) {
            builder.append(" ($colorName)")
        }
        return builder.toString()
    }
}

fun ChipboardUi.toChipboard(): Chipboard {
    return Chipboard(
        id = id,
        unionId = unionId,
        quantity = quantity,
        colorName = colorName,
        color = color,
        size1 = size1,
        size2 = size2,
        size3 = size3
    )
}

fun Chipboard.toChipboardUi(): ChipboardUi {
    return ChipboardUi(
        id = id,
        unionId = unionId,
        colorName = colorName,
        color = color,
        quantity = quantity,
        size1 = size1,
        size2 = size2,
        size3 = size3
    )
}