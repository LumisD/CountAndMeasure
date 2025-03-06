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
            parentColumns = ["union_id"],
            childColumns = ["union_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("union_id")]
)
data class Chipboard(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "chipboard_id")
    val chipboardId: Int,
    @ColumnInfo(name = "union_id")
    val unionId: Int, // Foreign key
    val width: Float,
    val length: Float,
    val height: Float,
    val color: String,
    val direction: Short,//0 - no direction; 1 - width is direction; 2 - length is direction; 3 - height is direction
    val quantity: Short
)