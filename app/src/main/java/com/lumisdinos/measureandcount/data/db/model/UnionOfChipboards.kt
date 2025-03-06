package com.lumisdinos.measureandcount.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "union_of_chipboards")
data class UnionOfChipboards(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "union_id")
    val unionId: Int,
    val title: String
)