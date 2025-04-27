package com.lumisdinos.measureandcount.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lumisdinos.measureandcount.ui.model.UnionOfChipboardsUI

@Entity(tableName = "union_of_chipboards")
data class UnionOfChipboards(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,
    val title: String,
    val isFinished: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)

fun UnionOfChipboards.toUnionOfChipboardsUI(): UnionOfChipboardsUI {
    return UnionOfChipboardsUI(
        id = id,
        title = title,
        isFinished = isFinished,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}