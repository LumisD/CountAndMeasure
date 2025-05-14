package com.lumisdinos.measureandcount.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lumisdinos.measureandcount.ui.model.UnionOfChipboardsUI

@Entity(tableName = "union_of_chipboards")
data class UnionOfChipboards(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    val title: String,
    val dimensions: Int,//qty of dimensions starts from 1
    val direction: Int,//0 - no direction, 1 to n - direction column
    @ColumnInfo(name = "has_color")
    val hasColor: Boolean,
    @ColumnInfo(name = "is_finished")
    val isFinished: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)