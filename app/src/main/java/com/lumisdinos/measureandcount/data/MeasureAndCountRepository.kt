package com.lumisdinos.measureandcount.data

import com.lumisdinos.measureandcount.data.db.model.Chipboard
import com.lumisdinos.measureandcount.data.db.model.UnionOfChipboards
import kotlinx.coroutines.flow.Flow

interface MeasureAndCountRepository {

    suspend fun insertUnionOfChipboards(unionOfChipboards: UnionOfChipboards): Int

    suspend fun updateUnionOfChipboardsTitle(unionId: Int, newTitle: String, updatedAt: Long)

    suspend fun setUnionOfChipboardsFinished(unionId: Int, isFinished: Boolean, updatedAt: Long)

    suspend fun getUnionOfChipboardsById(unionId: Int): UnionOfChipboards?

    suspend fun getLastUnFinishedUnionOfChipboards(): UnionOfChipboards?

    suspend fun deleteUnionOfChipboards(unionId: Int)

    suspend fun insertChipboard(chipboard: Chipboard)

    suspend fun updateChipboardState(id: Int, newState: Int)

    suspend fun getChipboardsCountByUnionId(unionId: Int): Int

    suspend fun deleteChipboardById(chipboardId: Int)

    fun getChipboardsByUnionIdFlow(unionId: Int): Flow<List<Chipboard>>

}