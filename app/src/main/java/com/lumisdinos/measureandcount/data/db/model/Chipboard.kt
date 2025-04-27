package com.lumisdinos.measureandcount.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.lumisdinos.measureandcount.ui.model.ChipboardUi

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
    val dimensions: Short,//qty of dimensions starts from 1
    val color: String,
    val direction: Short,//0 - no direction, 1 to n - direction column
    val quantity: Short,
    val title1: String,
    val size1: Float,
    val title2: String,
    val size2: Float,
    val title3: String,
    val size3: Float,
    val title4: String,
    val size4: Float,
    val title5: String,
    val size5: Float
    )

fun Chipboard.toChipboardUi(): ChipboardUi {
    return ChipboardUi(
        id = id,
        unionId = unionId,
        dimensions = dimensions,
        color = color,
        direction = direction,
        quantity = quantity,
        title1 = title1,
        size1 = size1,
        title2 = title2,
        size2 = size2,
        title3 = title3,
        size3 = size3,
        title4 = title4,
        size4 = size4,
        title5 = title5,
        size5 = size5
    )
}