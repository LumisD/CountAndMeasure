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

    override suspend fun insertAndGetUnionOfChipboards(union: UnionOfChipboards): UnionOfChipboards? {
        val unionId = unionOfChipboardsDao.insertUnionOfChipboards(union)
        if (unionId > 0) {
            return unionOfChipboardsDao.getUnionOfChipboardsById(unionId.toInt())
        }
        return null
    }

    override suspend fun updateUnionOfChipboardsTitle(
        unionId: Int,
        newTitle: String,
        updatedAt: Long
    ) {
        unionOfChipboardsDao.updateUnionOfChipboardsTitle(unionId, newTitle, updatedAt)
    }

    override suspend fun updateUnionCharacteristics(
        unionId: Int,
        dimensions: Int,
        direction: Int,
        hasColor: Boolean,
        titleColumn1: String,
        titleColumn2: String,
        titleColumn3: String
    ) {
        unionOfChipboardsDao.updateUnionCharacteristics(
            unionId,
            dimensions,
            direction,
            hasColor,
            titleColumn1,
            titleColumn2,
            titleColumn3
        )
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
            color = chipboard.color,
            colorName = chipboard.colorName,
            size1 = chipboard.size1,
            realSize1 = chipboard.realSize1,
            size2 = chipboard.size2,
            realSize2 = chipboard.realSize2,
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