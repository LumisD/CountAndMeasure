package com.lumisdinos.measureandcount.data

import com.lumisdinos.measureandcount.data.db.ChipboardDao
import com.lumisdinos.measureandcount.data.db.UnionOfChipboardsDao
import com.lumisdinos.measureandcount.data.db.model.Chipboard
import com.lumisdinos.measureandcount.data.db.model.UnionOfChipboards
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class MeasureAndCountRepositoryImpl @Inject constructor(
    private val unionOfChipboardsDao: UnionOfChipboardsDao,
    private val chipboardDao: ChipboardDao
) : MeasureAndCountRepository {

    override suspend fun insertUnionOfChipboards(unionOfChipboards: UnionOfChipboards): Int {
        return unionOfChipboardsDao.insertUnionOfChipboards(unionOfChipboards).toInt()
    }

    override suspend fun updateUnionOfChipboardsTitle(
        unionId: Int,
        newTitle: String,
        updatedAt: Long
    ) {
        unionOfChipboardsDao.updateUnionOfChipboardsTitle(unionId, newTitle, updatedAt)
    }

    override suspend fun setUnionOfChipboardsIsFinished(unionId: Int, isFinished: Boolean, updatedAt: Long) {
        unionOfChipboardsDao.setUnionOfChipboardsIsFinished(unionId, isFinished, updatedAt)
    }

    override suspend fun getUnionOfChipboardsById(unionId: Int): UnionOfChipboards? {
        return unionOfChipboardsDao.getUnionOfChipboardsById(unionId)
    }

    override suspend fun getLastUnFinishedUnionOfChipboards(): UnionOfChipboards? {
        return unionOfChipboardsDao.getLastUnFinishedUnionOfChipboards()
    }

    override suspend fun deleteUnionOfChipboards(unionId: Int) {
        unionOfChipboardsDao.deleteUnionOfChipboardsById(unionId)

    }

    override suspend fun insertChipboard(chipboard: Chipboard) {
        chipboardDao.insertChipboard(chipboard)
    }

    override suspend fun updateChipboardState(id: Int, newState: Int) {
        chipboardDao.updateChipboardState(id, newState)
    }

    override suspend fun getChipboardsCountByUnionId(unionId: Int): Int {
        return chipboardDao.getChipboardsCountByUnionId(unionId)
    }

    override suspend fun getQuantityOfChipboardByConditions(id: Int, unionId: Int, state: Int): Short {
        // Call the DAO method that returns Short?
        val quantityFromDb = chipboardDao.getQuantityOfChipboardByConditions(id, unionId, state)
        // Check if the result is null and return -1 if it is, otherwise return the quantity
        return quantityFromDb ?: -1
    }

    override suspend fun deleteChipboardById(chipboardId: Int) {
        chipboardDao.deleteChipboardById(chipboardId)
    }

    override fun getChipboardsByUnionIdFlow(unionId: Int): Flow<List<Chipboard>> {
        return chipboardDao.getChipboardsFlowByUnionId(unionId)
    }

}