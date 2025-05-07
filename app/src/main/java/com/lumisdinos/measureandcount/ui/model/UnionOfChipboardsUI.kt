package com.lumisdinos.measureandcount.ui.model

import com.lumisdinos.measureandcount.data.db.model.UnionOfChipboards

data class UnionOfChipboardsUI(
    var id: Int = 0,
    var title: String = "",
    var isFinished: Boolean = false,
    var createdAt: Long = 0L,
    var updatedAt: Long = 0L
)

fun UnionOfChipboardsUI.toUnionOfChipboards(): UnionOfChipboards {
    return UnionOfChipboards(
        id = id,
        title = title,
        isFinished = isFinished,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun UnionOfChipboards.toUnionOfChipboardsUI(): UnionOfChipboardsUI {
    return UnionOfChipboardsUI(
        id = id,
        title = title,
        isFinished = isFinished,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}