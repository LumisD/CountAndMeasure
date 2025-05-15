package com.lumisdinos.measureandcount.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
    @ColumnInfo(name = "title_column1")
    val titleColumn1: String,
    @ColumnInfo(name = "title_column2")
    val titleColumn2: String,
    @ColumnInfo(name = "title_column3")
    val titleColumn3: String,
    @ColumnInfo(name = "is_finished")
    val isFinished: Boolean,
    @ColumnInfo(name = "is_marked_as_deleted")
    val isMarkedAsDeleted: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)