package com.lumisdinos.measureandcount.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chipboard",
    foreignKeys = [
        ForeignKey(
            entity = UnionOfChipboards::class,
            parentColumns = ["id"],
            childColumns = ["union_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("union_id")]
)
data class Chipboard(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "union_id")
    val unionId: Int,
    val state: Int = 0,//0 - not found, 1 - found, 2 - unknown
    val dimensions: Short,//qty of dimensions starts from 1
    val direction: Short,//0 - no direction, 1 to n - direction column
    val quantity: Short,
    val colorName: String,
    val color: Int,
    val title1: String,
    val size1: Float,
    val realSize1: Float = 0f,//diff between real measured size and size1
    val title2: String,
    val size2: Float,
    val realSize2: Float = 0f,
    val title3: String,
    val size3: Float,
    val realSize3: Float = 0f
)

