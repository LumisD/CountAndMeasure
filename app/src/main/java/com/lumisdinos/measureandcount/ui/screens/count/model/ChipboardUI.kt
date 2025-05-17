package com.lumisdinos.measureandcount.ui.screens.count.model

import com.lumisdinos.measureandcount.data.db.model.Chipboard
import com.lumisdinos.measureandcount.ui.model.Shareable
import com.lumisdinos.measureandcount.ui.model.UnionOfChipboardsUI

data class ChipboardUi(
    val id: Int = 0,
    val unionId: Int = 0,
    val state: Int = 2,//0 - not found, 1 - found, 2 - unknown
    val quantity: Int = 1,
    val colorName: String = "",
    val color: Int = 0,
    val size1: Float = 0f,
    val realSize1: Float = 0f,//diff between real measured size and size1
    val size2: Float = 0f,
    val realSize2: Float = 0f,
    val size3: Float = 0f,
    val realSize3: Float = 0f,

    val quantityAsString: String = "1",
    val size1AsString: String = "",
    val real1AsString: String = "",
    val size2AsString: String = "",
    val real2AsString: String = "",
    val size3AsString: String = "",
    val real3AsString: String = "",
    val chipboardAsString: String = "",
    val allRealsAsString: String = "",

    val isUnderReview: Boolean = false//if true - enable: Found button, "real size" editors
    // AND disable: Unknown button,size editors, quantity editor, color editor
    //if false - all opposite
) : Shareable {

    override fun getShareableString(union: UnionOfChipboardsUI): String {
        val builder = StringBuilder()
        val realSizeBuilder = StringBuilder()
        val dimensions = union.dimensions
        val direction = union.direction
        var isAllRealsEmpty = true

        for (i in 1..dimensions) {
            if (direction == i) {
                builder.append("↑")
                realSizeBuilder.append(" ")
            }

            val sizeString = when (i) {
                1 -> size1.toString()
                2 -> size2.toString()
                3 -> size3.toString()
                else -> ""
            }
            builder.append(sizeString)

            val realSize = when (i) {
                1 -> realSize1
                2 -> realSize2
                3 -> realSize3
                else -> 0f
            }
            if (realSize != 0f) {
                isAllRealsEmpty = false
                val realSizeString = realSize.toString()
                realSizeBuilder.append(realSizeString)
                if (sizeString.length > realSizeString.length) {
                    realSizeBuilder.append(" ".repeat(sizeString.length - realSizeString.length))
                }
            } else {
                realSizeBuilder.append(" ".repeat(sizeString.length))
            }


            if (i < dimensions) {
                builder.append(" x ")
                realSizeBuilder.append("    ")
            }
        }
        builder.append(" - $quantity")
        if (union.hasColor && colorName.isNotBlank()) {
            builder.append(" ($colorName)")
        }
        val stateText = when (state) {
            0 -> " (Not Found)"
            1 -> " (Found)"
            else -> ""
        }
        builder.append(stateText)

        if (!isAllRealsEmpty) {
            //so, final result will consist of two lines:
            //↑12.5 x 54.0 - 3 White (Found)
            // 12.1   52
            realSizeBuilder.append("      <- real size")
            builder.appendLine()
            builder.append(realSizeBuilder.toString().trimEnd())

        }

        return builder.toString()
    }

}

fun ChipboardUi.toChipboard(): Chipboard {
    return Chipboard(
        id = id,
        unionId = unionId,
        state = state,
        quantity = quantity,
        colorName = colorName,
        color = color,
        size1 = size1,
        realSize1 = realSize1,
        size2 = size2,
        realSize2 = realSize2,
        size3 = size3,
        realSize3 = realSize3
    )
}

fun Chipboard.toChipboardUi(): ChipboardUi {
    return ChipboardUi(
        id = id,
        unionId = unionId,
        state = state,
        quantity = quantity,
        colorName = colorName,
        color = color,
        size1 = size1,
        realSize1 = realSize1,
        size2 = size2,
        realSize2 = realSize2,
        size3 = size3,
        realSize3 = realSize3
    )
}