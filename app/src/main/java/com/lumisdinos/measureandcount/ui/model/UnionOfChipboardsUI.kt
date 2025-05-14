package com.lumisdinos.measureandcount.ui.model

import com.lumisdinos.measureandcount.data.db.model.UnionOfChipboards

data class UnionOfChipboardsUI(
    var id: Int = 0,
    var title: String = "",
    val dimensions: Int = 1,//qty of dimensions starts from 1
    val direction: Int = 0,//0 - no direction, 1 to n - direction column
    val hasColor: Boolean = false,
    var isFinished: Boolean = false,
    var createdAt: Long = 0L,
    var updatedAt: Long = 0L
)

fun UnionOfChipboardsUI.toUnionOfChipboards(): UnionOfChipboards {
    return UnionOfChipboards(
        id = id,
        title = title,
        dimensions = dimensions,
        direction = direction,
        hasColor = hasColor,
        isFinished = isFinished,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun UnionOfChipboards.toUnionOfChipboardsUI(): UnionOfChipboardsUI {
    return UnionOfChipboardsUI(
        id = id,
        title = title,
        dimensions = dimensions,
        direction = direction,
        hasColor = hasColor,
        isFinished = isFinished,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}