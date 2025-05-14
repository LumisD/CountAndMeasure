package com.lumisdinos.measureandcount.data

import com.lumisdinos.measureandcount.data.db.model.Chipboard
import com.lumisdinos.measureandcount.data.db.model.UnionOfChipboards
import kotlinx.coroutines.flow.Flow

interface MeasureAndCountRepository {

    suspend fun insertUnionOfChipboards(unionOfChipboards: UnionOfChipboards): Int

    suspend fun insertAndGetUnionOfChipboards(union: UnionOfChipboards): UnionOfChipboards?

    suspend fun updateUnionOfChipboardsTitle(unionId: Int, newTitle: String, updatedAt: Long)

    suspend fun updateUnionCharacteristics(
        unionId: Int,
        dimensions: Int,
        direction: Int,
        hasColor: Boolean,
        titleColumn1: String,
        titleColumn2: String,
        titleColumn3: String
    )

    suspend fun setUnionOfChipboardsIsFinished(unionId: Int, isFinished: Boolean, updatedAt: Long)

    suspend fun getUnionOfChipboardsById(unionId: Int): UnionOfChipboards?

    suspend fun getLastUnFinishedUnionOfChipboards(): UnionOfChipboards?

    suspend fun deleteUnionOfChipboards(unionId: Int)

    fun getAllUnionsFlow(): Flow<List<UnionOfChipboards>>


    suspend fun insertChipboard(chipboard: Chipboard)

    suspend fun updateChipboardState(id: Int, newState: Int)

    suspend fun updateChipboardQuantity(id: Int, newQuantity: Int)

    suspend fun findSimilarFoundChipboard(chipboard: Chipboard): Chipboard?

    suspend fun getChipboardByIdAndUnionId(chipboardId: Int, unionId: Int): Chipboard?

    suspend fun getChipboardsCountByUnionId(unionId: Int): Int

    suspend fun getQuantityOfChipboardByConditions(id: Int, unionId: Int, state: Int): Int

    suspend fun deleteChipboardById(chipboardId: Int)

    fun getChipboardsByUnionIdFlow(unionId: Int): Flow<List<Chipboard>>

}