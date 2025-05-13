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

    override suspend fun setUnionOfChipboardsIsFinished(
        unionId: Int,
        isFinished: Boolean,
        updatedAt: Long
    ) {
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

    override fun getAllUnionsFlow(): Flow<List<UnionOfChipboards>> {
        return unionOfChipboardsDao.getAllUnionsFlow()
    }



    override suspend fun insertChipboard(chipboard: Chipboard) {
        chipboardDao.insertChipboard(chipboard)
    }

    override suspend fun updateChipboardState(id: Int, newState: Int) {
        chipboardDao.updateChipboardState(id, newState)
    }

    override suspend fun updateChipboardQuantity(id: Int, newQuantity: Int) {
        chipboardDao.updateChipboardQuantity(id, newQuantity)
    }

    override suspend fun findSimilarFoundChipboard(chipboard: Chipboard): Chipboard? {
        return chipboardDao.findSimilarFoundChipboard(
            unionId = chipboard.unionId,
            chipboardId = chipboard.id,
            dimensions = chipboard.dimensions,
            direction = chipboard.direction,
            color = chipboard.color,
            colorName = chipboard.colorName,
            title1 = chipboard.title1,
            size1 = chipboard.size1,
            realSize1 = chipboard.realSize1,
            title2 = chipboard.title2,
            size2 = chipboard.size2,
            realSize2 = chipboard.realSize2,
            title3 = chipboard.title3,
            size3 = chipboard.size3,
            realSize3 = chipboard.realSize3
        )
    }

    override suspend fun getChipboardByIdAndUnionId(chipboardId: Int, unionId: Int): Chipboard? {
        return chipboardDao.getChipboardByIdAndUnionId(chipboardId, unionId)
    }

    override suspend fun getChipboardsCountByUnionId(unionId: Int): Int {
        return chipboardDao.getChipboardsCountByUnionId(unionId)
    }

    override suspend fun getQuantityOfChipboardByConditions(
        id: Int,
        unionId: Int,
        state: Int
    ): Int {
        val quantityFromDb = chipboardDao.getQuantityOfChipboardByConditions(id, unionId, state)
        return quantityFromDb ?: -1
    }

    override suspend fun deleteChipboardById(chipboardId: Int) {
        chipboardDao.deleteChipboardById(chipboardId)
    }

    override fun getChipboardsByUnionIdFlow(unionId: Int): Flow<List<Chipboard>> {
        return chipboardDao.getChipboardsFlowByUnionId(unionId)
    }

}