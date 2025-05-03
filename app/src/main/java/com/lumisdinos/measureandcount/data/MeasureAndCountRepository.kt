package com.lumisdinos.measureandcount.data

import com.lumisdinos.measureandcount.data.db.model.Chipboard
import com.lumisdinos.measureandcount.data.db.model.UnionOfChipboards
import kotlinx.coroutines.flow.Flow

interface MeasureAndCountRepository {

    suspend fun insertChipboard(chipboard: Chipboard)

    suspend fun insertUnionOfChipboards(unionOfChipboards: UnionOfChipboards): Int

    suspend fun updateUnionOfChipboardsTitle(unionId: Int, newTitle: String, updatedAt: Long)

    suspend fun getChipboardsCountByUnionId(unionId: Int): Int

    suspend fun deleteChipboardById(chipboardId: Int)

    suspend fun deleteUnionOfChipboards(unionId: Int)

    fun getChipboardsByUnionIdFlow(unionId: Int): Flow<List<Chipboard>>

}