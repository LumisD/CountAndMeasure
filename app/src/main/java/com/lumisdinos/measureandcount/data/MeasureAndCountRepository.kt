package com.lumisdinos.measureandcount.data

import com.lumisdinos.measureandcount.ui.model.ChipboardUi
import com.lumisdinos.measureandcount.ui.model.UnionOfChipboardsUI
import kotlinx.coroutines.flow.Flow

interface MeasureAndCountRepository {

    suspend fun insertChipboard(chipboardUi: ChipboardUi)

    suspend fun insertUnionOfChipboards(unionOfChipboardsUI: UnionOfChipboardsUI): Int

    suspend fun updateUnionOfChipboardsTitle(unionId: Int, newTitle: String, updatedAt: Long)

    suspend fun getChipboardsCountByUnionId(unionId: Int): Int

    suspend fun deleteChipboardById(chipboardId: Int)

    suspend fun deleteUnionOfChipboards(unionId: Int)

    fun getChipboardsByUnionIdFlow(unionId: Int): Flow<List<ChipboardUi>>

}